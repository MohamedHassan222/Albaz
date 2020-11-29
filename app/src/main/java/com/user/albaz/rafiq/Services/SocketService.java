package com.user.albaz.rafiq.Services;


import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Settings;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.user.albaz.rafiq.App;
import com.user.albaz.rafiq.Listeners.OnReceiveSocketListener;
import com.user.albaz.rafiq.Listeners.ShowMap;
import com.user.albaz.rafiq.R;
import com.user.albaz.rafiq.activities.CongratulationActivity;
import com.user.albaz.rafiq.activities.MainActivity;
import com.user.albaz.rafiq.fragments.HomeFragment;
import com.user.albaz.rafiq.helper.SharedHelper;
import com.user.albaz.rafiq.models.Provider;
import com.user.albaz.rafiq.utils.Utilities;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.google.android.gms.location.LocationServices;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.user.albaz.rafiq.activities.MainActivity.IsActivityAlive;
import static com.user.albaz.rafiq.fragments.HomeFragment.IsMapInView;
import static com.user.albaz.rafiq.fragments.HomeFragment.flow;


// implement this service to handle socket
public class SocketService extends Service {

    public static com.github.nkzawa.socketio.client.Socket socket;

    public static final String ACTION_START_FOREGROUND_SERVICE = "let us go client ";
    public static final String ACTION_STOP_FOREGROUND_SERVICE = "shut it down client";
    public static final Integer ServiceId = 3345;
    public static String CHANNEL_ID = "channel_ailbaz_client";
    public static String CHANNEL_NAME = "connected_ailbaz_client";
    static Boolean ScheduleClicked = false;


    public static String mProviderId = "0";
    private static String mEmail;
    private static String mPhone;
    private static String fName ;

    CountDownTimer countDownTimer;
    MediaPlayer mPlayer;

    TextView txt01Timer, txt01UserName, txt01Address, toAddressTextView, fromAddressTextView, tripDateTextView, tripTimeTextView;
    CircleImageView img01User;
    Button btn_02_accept, btn_02_reject;

    Button overlyView = null;
    View requestView;
    WindowManager manager;

    public enum SocketMassageType {
        found,
        arrived,
        pickedUp,
        provider_cancel_your_trip_is_searching,
        provider_cancel_trip_after_waiting,
        your_trip_is_canceld_from_providers,
        user_cancel_trip_after_waiting,
        dropped,
        Paid,
        bill,
        ComfirmPaid,
        TimeOut
    }

    Location location;
    Intent dialogIntent;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        super.onStartCommand(intent, flags, startId);
        dialogIntent = new Intent(this, MainActivity.class);
        if (intent != null) {
            String action = intent.getAction();
            switch (action) {
                case ACTION_START_FOREGROUND_SERVICE:
                    try {
                        startSocket();

                    } catch (URISyntaxException e) {
                        Toast.makeText(getApplicationContext(), "Server Error", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                    break;
                case ACTION_STOP_FOREGROUND_SERVICE:
                    stopService();
                    break;

                default:
                    stopService();
                    break;

            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            askPermission();
        }
        return START_STICKY;
    }

    @TargetApi(23)
    public void askPermission() {

    }

    // start service
    public void startSocket() throws URISyntaxException {

        startForeground(ServiceId, buildNotification());
        checkPermission();

        final Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    checkPermission();
                    location = LocationServices.FusedLocationApi.getLastLocation(HomeFragment.mGoogleApiClient);
                    initializeSocket();
                } catch (Exception e) {
                    handler.postDelayed(this, 2000);
                }
            }
        });

        startShutDownFunction();

    }

    private void startShutDownFunction() {

        countDownTimer = new CountDownTimer(60000, 3000) {
            @Override
            public void onTick(long l) {

                if (flow != HomeFragment.checkStatusFlow.SEARCHING) {
                    countDownTimer.cancel();
                }
            }

            @Override
            public void onFinish() {
                try {
                    if (flow == HomeFragment.checkStatusFlow.SEARCHING) {
                        doAfterRequestConfirmed(SocketMassageType.TimeOut, null);
                        socket.emit("delete_request", SharedHelper.getKey(getApplicationContext(), "id"));
                    }
                } catch (Exception e) {

                }
            }
        };
        countDownTimer.start();
    }


    private void stopService() {
        stopForeground(true);
        try {
            socket.disconnect();
        } catch (Exception e) {

        }
        stopSelf();
    }


    public void checkPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
    }


    private void initializeSocket() {

        IO.Options opts = new IO.Options();
        opts.timeout = 10000;
        opts.forceNew = true;

        try {
            String id = SharedHelper.getKey(getApplicationContext(), "id");
            id = id.replace("\"", "");
            socket = IO.socket("http://5.189.186.251:3000?type=Clients"

                    +"&lat=" + location.getLatitude() +
                    "&long=" + location.getLongitude() +
                    "&id=" + Integer.parseInt(id), opts);
//            socket = IO.socket(getString(R.string.socket_io_new)
//
//                    +"&lat=" + location.getLatitude() +
//                    "&long=" + location.getLongitude() +
//                    "&id=" + Integer.parseInt(id), opts);


        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        // Create Connection
        socket.connect();
        setupEmitterListener();
    }

    //setup Emitter Listeners
    private void setupEmitterListener() {

        //when a driver accept request
        socket.on("found", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {

                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        doAfterRequestConfirmed(SocketMassageType.found, (JSONObject) args[0]);

                    }
                });

            }
        });


        socket.on("accept", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {


                        JSONObject request = (JSONObject) args[0];
                        mEmail = request.optJSONObject("request").optString("email");
                        mPhone = request.optJSONObject("request").optString("mobile");
                        mProviderId = request.optJSONObject("response").optString("id");
                        fName = request.optJSONObject("response").optString("first_name");


                        if (true) {
                            doAfterRequestConfirmed(SocketMassageType.found, (JSONObject) args[0]);
                        } else
                            Toast.makeText(getBaseContext(), "arrived Not yours TripModel", Toast.LENGTH_SHORT).show();

                   }
                });

            }
        });

        //when a driver arrived
        socket.on("arrived", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {

                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (true) {
                                showArrivedView((JSONObject) args[0]);
                            doAfterRequestConfirmed(SocketMassageType.arrived, (JSONObject) args[0]);
                        } else
                            Toast.makeText(getBaseContext(), "arrived Not yours TripModel", Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });

        socket.on("pickedUp", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        if (manager != null) {
                            if (requestView.isShown())
                                manager.removeView(requestView);
                        }
                        if (mPlayer != null && mPlayer.isPlaying()) {
                            mPlayer.stop();
                            mPlayer = null;
                        }
                        doAfterRequestConfirmed(SocketMassageType.pickedUp, (JSONObject) args[0]);
                        Toast.makeText(getBaseContext(), "Enjoy your TripModel", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        socket.on("provider_cancel_your_trip_is_searching", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        doAfterRequestConfirmed(SocketMassageType.provider_cancel_your_trip_is_searching, (JSONObject) args[0]);
                    }
                });
            }
        });

              socket.on("provider_cancel_trip_after_waiting", new Emitter.Listener() {

            @Override
            public void call(final Object... args) {

                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        doAfterRequestConfirmed(SocketMassageType.provider_cancel_trip_after_waiting, (JSONObject) args[0]);

                    }
                });
            }
        });

                //
              socket.on("user_cancel_trip_after_waiting", new Emitter.Listener() {


            @Override
            public void call(final Object... args) {

                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        doAfterRequestConfirmed(SocketMassageType.user_cancel_trip_after_waiting, (JSONObject) args[0]);


                    }
                });
            }
        });

        //your_trip_is_canceld_from_providers
        socket.on("your_trip_is_canceld_from_providers", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        doAfterRequestConfirmed(SocketMassageType.your_trip_is_canceld_from_providers, (JSONObject) args[0]);
                    }
                });
            }
        });

        //when they reach final destination
        socket.on("dropped", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {

                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject request = (JSONObject) args[0];
                        mEmail = request.optJSONObject("request").optString("email");
                        mPhone = request.optJSONObject("request").optString("mobile");
                        if (true) {
                            doAfterRequestConfirmed(SocketMassageType.dropped, (JSONObject) args[0]);
                        } else
                            Toast.makeText(getBaseContext(), "dropped Not yours TripModel", Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });
        socket.on("Paid", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject request = (JSONObject) args[0];
                        mEmail = request.optJSONObject("request").optString("email");
                        mPhone = request.optJSONObject("request").optString("mobile");
                        if (true) {
                            doAfterRequestConfirmed(SocketMassageType.Paid, (JSONObject) args[0]);
                        } else
                            Toast.makeText(getBaseContext(), "Paid Not yours TripModel", Toast.LENGTH_SHORT).show();


                    }
                });
            }
        });

        socket.on("drop", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject request = (JSONObject) args[0];
                        mEmail = request.optJSONObject("request").optString("email");
                        mPhone = request.optJSONObject("request").optString("mobile");
                        if (true) {
                            doAfterRequestConfirmed(SocketMassageType.Paid, (JSONObject) args[0]);
                        }

                    }
                });
            }
        });
        socket.on("bill", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject request = (JSONObject) args[0];
                        JSONObject bill = request.optJSONObject("bill");

                        if (true) {
                            doAfterRequestConfirmed(SocketMassageType.bill, bill);
                        }

                    }
                });
            }
        });


        socket.on("ConfirmPaid", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject request = (JSONObject) args[0];

                        if (true) {
                            doAfterRequestConfirmed(SocketMassageType.ComfirmPaid, (JSONObject) args[0]);
                        }


                    }
                });
            }
        });
        socket.on("current_schedule_trip", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void run() {
                        JSONObject request = (JSONObject) args[0];

                        JSONObject schedule = request.optJSONObject("schedule");
                        if (Utilities.Trip_Type == 0) {
                            showRequestView(schedule);
                            Utilities.Trip_Type = 1;
                        }

                    }
                });
            }
        });
        socket.on("confirm_save_schedule_trip", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject request = (JSONObject) args[0];
                        JSONObject schedule = request.optJSONObject("schedule");

                        if (true) {
                            String msa = null, status = null;
                            try {
                                msa = schedule.getString("message");
                                status = schedule.getString("status");
                                Intent i = new Intent(getApplicationContext(), CongratulationActivity.class);
                                i.putExtra("trip", "schedule");
                                i.putExtra("status", status);
                                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(i);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }


                    }
                });
            }
        });


    }

    private void doAfterRequestConfirmed(final SocketMassageType massageType, final JSONObject jsonObject) {
        Log.d("etttttttttttttttttt","act"+IsActivityAlive  +" map in view "+IsMapInView);

        if (IsActivityAlive) {
            if (IsMapInView) {
                if (HomeFragment.onReceiveSocketListener!=null)
                {
                    final OnReceiveSocketListener on = HomeFragment.onReceiveSocketListener;
                    on.doAfterReceiveSocketData(massageType, jsonObject, true);

                    Log.d("errrrrrrrrrrrrrrrr","not null"); }
                else
                {
                    Log.d("errrrrrrrrrrrrrrrr","is null");
                }

            } else {
                ShowMap showMap = new MainActivity();
                showMap.showMap();
                final Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //call function
                        if (IsMapInView) {
                            final OnReceiveSocketListener on = HomeFragment.onReceiveSocketListener;
                            on.doAfterReceiveSocketData(massageType, jsonObject, false);
                            return;
                        } else {
                            handler.postDelayed(this, 2000);
                        }

                    }
                }, 2000);
            }
        } else {

            dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(dialogIntent);
            final Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //call function
                    if (IsMapInView) {
                        final OnReceiveSocketListener on = HomeFragment.onReceiveSocketListener;
                        on.doAfterReceiveSocketData(massageType, jsonObject, false);
                        return;
                    } else {
                        handler.postDelayed(this, 2000);
                    }
                }
            }, 2000);
        }
    }

    private Notification buildNotification() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }


        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID);
        builder.setSmallIcon(R.drawable.ic_menu_send)
                .setContentTitle("Ailbas Client")
                .setContentText("welcome from ailbaz")
                .setWhen(new Date().getTime())
                .setPriority(NotificationCompat.PRIORITY_HIGH);
        return builder.build();

    }

    private void showRequestView2(final JsonObject request) {

        final Dialog dialog = new Dialog(getApplication());
        dialog.setContentView(R.layout.dialog_edit);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        TextView msg = dialog.findViewById(R.id.msg);
        TextView details = dialog.findViewById(R.id.details);
        LinearLayout yes = dialog.findViewById(R.id.yes);
        LinearLayout no = dialog.findViewById(R.id.no);


        msg.setText(getString(R.string.have_schedule_trip));
        details.setText((R.string.confirm_sechedule));
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                JSONObject requestObj = new JSONObject();
                try {
                    requestObj.put("confirm", "1");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (socket != null) {
                    socket.emit("confirm_schedule_trip", requestObj);
                }
                dialog.dismiss();
            }
        });

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject requestObj = new JSONObject();
                try {
                    requestObj.put("confirm", "0");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (socket != null) {
                    socket.emit("confirm_schedule_trip", requestObj);
                }
                dialog.dismiss();
            }
        });
        dialog.show();


    }

    private void showRequestView(final JSONObject request) {
        requestView = new LinearLayout(this);

        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        requestView = inflater.inflate(R.layout.request_overly_view_layout, null);

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void run() {
                makeRequestView(requestView, request);
                manager = (WindowManager) getSystemService(WINDOW_SERVICE);
                int LAYOUT_FLAG;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
                } else {
                    LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
                }


                WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                        WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        LAYOUT_FLAG,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                        PixelFormat.TRANSLUCENT);
                params.gravity = Gravity.BOTTOM;
                if (true) {
                    Utilities.IsReuestShown = true;
                    manager.addView(requestView, params);
                }
            }
        });

    }

    private void showArrivedView(final JSONObject request) {
        if (mPlayer == null) {
            mPlayer = MediaPlayer.create(App.getInstance(), R.raw.user_alarm);
        } else if (!mPlayer.isPlaying()) {
            mPlayer.start();
        }


        requestView = new LinearLayout(this);
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        requestView = inflater.inflate(R.layout.request_arrived_view_layout, null);

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void run() {
                makeArrivedView(requestView, request);
                manager = (WindowManager) getSystemService(WINDOW_SERVICE);
                int LAYOUT_FLAG;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
                } else {
                    LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
                }


                WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                        WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        LAYOUT_FLAG,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                        PixelFormat.TRANSLUCENT);
                params.gravity = Gravity.BOTTOM;
                if (true) {
                    Utilities.IsReuestShown = true;
                    manager.addView(requestView, params);
                }
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void makeRequestView(final View requestView, final JSONObject request) {
        txt01Timer = requestView.findViewById(R.id.txt01Timer);
        img01User = requestView.findViewById(R.id.img01User);
        txt01UserName = requestView.findViewById(R.id.txt01UserName);
        tripDateTextView = requestView.findViewById(R.id.txtDateTrip);
        tripTimeTextView = requestView.findViewById(R.id.txtTimeTrip);
        toAddressTextView = requestView.findViewById(R.id.to_address_tv);
        fromAddressTextView = requestView.findViewById(R.id.from_address_tv);
        btn_02_accept = requestView.findViewById(R.id.btn_02_accept);
        btn_02_reject = requestView.findViewById(R.id.btn_02_reject);


        txt01UserName.setText(SharedHelper.getKey(getApplicationContext(), "first_name") + "  " +
                SharedHelper.getKey(getApplicationContext(), "last_name"));

        try {
            tripTimeTextView.setText(request.getString("schedule_time"));
            tripDateTextView.setText(request.getString("schedule_date"));
            toAddressTextView.setText(request.getString("d_address"));
            fromAddressTextView.setText(request.getString("s_address"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        btn_02_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ScheduleClicked = true;
                if (mPlayer != null && mPlayer.isPlaying()) {
                    mPlayer.stop();
                    mPlayer = null;

                }

                countDownTimer.cancel();
                manager.removeView(requestView);
                Utilities.IsReuestShown = false;
                JSONObject requestObj = new JSONObject();
                try {
                    requestObj = request;
                    requestObj.put("confirm", "1");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (socket != null) {
                    socket.emit("confirm_schedule_trip", requestObj);
                }
            }
        });
        btn_02_reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mPlayer != null && mPlayer.isPlaying()) {
                    mPlayer.stop();
                    mPlayer = null;
                }
                ScheduleClicked = true;

                countDownTimer.cancel();
                manager.removeView(requestView);

                JSONObject requestObj = new JSONObject();
                try {
                    requestObj.put("confirm", "0");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (socket != null) {
                    socket.emit("confirm_schedule_trip", requestObj);
                }


                try {
                    manager.removeView(requestView);
                } catch (Exception e) {

                }


            }
        });


        countDownTimer = new CountDownTimer(30000,
                200) {

            @SuppressLint("SetTextI18n")
            public void onTick(long millisUntilFinished) {
                txt01Timer.setText("" + millisUntilFinished / 1000);
                if (mPlayer == null) {
                    mPlayer = MediaPlayer.create(App.getInstance(), R.raw.user_alarm);
                } else {
                    if (!mPlayer.isPlaying()) {
                        mPlayer.start();
                    }
                }
            }

            public void onFinish() {
                txt01Timer.setText("0");

                if (mPlayer != null && mPlayer.isPlaying()) {
                    mPlayer.stop();
                    mPlayer = null;
                }
                if (!ScheduleClicked) {
                    JSONObject requestObj = new JSONObject();
                    try {
                        requestObj.put("confirm", "2");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (socket != null) {
                        socket.emit("confirm_schedule_trip", requestObj);
                    }
                }
                try {
                    manager.removeView(requestView);
                } catch (Exception e) {

                }


            }
        }.start();

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void makeArrivedView(final View requestView, final JSONObject request) {
        if (mPlayer == null) {
            mPlayer = MediaPlayer.create(App.getInstance(), R.raw.user_alarm);
        } else {
            if (!mPlayer.isPlaying()) {
                mPlayer.start();
            }


        }
        final Provider provider = new Provider(request);

        TextView lblProvider = requestView.findViewById(R.id.lblProvider);
        RatingBar ratingProvider = requestView.findViewById(R.id.ratingProvider);
        Button btnCall = requestView.findViewById(R.id.btnEdit);
        Button btnCancelTrip = requestView.findViewById(R.id.btnCancelTrip);

        lblProvider.setText(provider.getFirstName() + "  " + provider.getLastName());
        if (provider.getRating().equals(""))
            ratingProvider.setRating((float) 4.0);
        else
            ratingProvider.setRating(Float.parseFloat(provider.getRating()));

        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPlayer != null && mPlayer.isPlaying()) {
                    mPlayer.stop();
                    mPlayer = null;
                }

                countDownTimer.cancel();
                manager.removeView(requestView);

                Intent intentCall = new Intent(Intent.ACTION_DIAL);
                intentCall.setData(Uri.parse("tel:" + provider.getMobile()));
                intentCall.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                startActivity(intentCall);

            }
        });
        btnCancelTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (mPlayer != null && mPlayer.isPlaying()) {
                    mPlayer.stop();
                    mPlayer = null;
                }

                countDownTimer.cancel();
                manager.removeView(requestView);
                JSONObject requestObj = new JSONObject();
                try {
                    requestObj.put("reason", " ");
                    requestObj.put("Trip_id", SharedHelper.getKey(getApplication(), "request_id"));
                    requestObj.put("provider_id",provider.getId());
                    requestObj.put("client_id",  SharedHelper.getKey(getApplication(),"id"));


                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
                if (SocketService.socket != null) {
                    SocketService.socket.emit("cancel_trip_user", requestObj);

                }
                SharedHelper.putKey(getBaseContext(), "request_id", "");


                //Toast.makeText(getBaseContext(), "From Socket Service" , Toast.LENGTH_SHORT).show();

                Intent i = new Intent(getApplicationContext(), CongratulationActivity.class);
                i.putExtra("trip", "cancel");
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);

            }
        });


    }


}
