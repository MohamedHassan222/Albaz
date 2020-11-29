package com.user.albaz.rafiq.activities;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.user.albaz.rafiq.App;
import com.user.albaz.rafiq.R;
import com.user.albaz.rafiq.Services.SocketService;
import com.user.albaz.rafiq.helper.ConnectionHelper;
import com.user.albaz.rafiq.helper.LoadingDialog;
import com.user.albaz.rafiq.helper.SharedHelper;
import com.user.albaz.rafiq.helper.URLHelper;
import com.user.albaz.rafiq.models.Driver;
import com.user.albaz.rafiq.utils.MyBoldTextView;
import com.user.albaz.rafiq.utils.MyButton;
import com.user.albaz.rafiq.utils.Utilities;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.user.albaz.rafiq.App.trimMessage;

public class HistoryDetails extends AppCompatActivity {
    int tripId = 0;
    public JSONObject jsonObject;
    Activity activity;
    Context context;
    Boolean isInternet;
    ConnectionHelper helper;
    LoadingDialog loadingDialog;
    MyBoldTextView tripAmount;
    TextView tripDate, tripTime;
    MyBoldTextView paymentType;
    MyBoldTextView booking_id;
    MyBoldTextView tripComments;
    //    MyBoldTextView tripProviderName;
    MyBoldTextView tripSource;
    MyBoldTextView lblTotalPrice;
    MyBoldTextView lblBookingID;
    MyBoldTextView tripDestination;
    MyBoldTextView lblTitle;
    MyBoldTextView lblBasePrice;
    MyBoldTextView lblDistancePrice;
    MyBoldTextView lblTaxPrice;
    ImageView tripImg, paymentTypeImg, editDate, editTime;//            tripProviderImg,


    //    RatingBar tripProviderRating;
    LinearLayout sourceAndDestinationLayout, lnrComments, lnrUpcomingLayout;
    View viewLayout;
    ImageView backArrow;
    LinearLayout parentLayout;
    LinearLayout profileLayout;
    LinearLayout lnrInvoice, lnrInvoiceSub;
    String tag = "";
    MyButton btnCancelRide;
    Driver driver;
    String reason = "";

    Button btnViewInvoice, btnEdit, btnConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_details);

        // Invoke Initialize Elements & Clicked
        findViewByIdAndInitialize();
        actions();
        try {
            Intent intent = getIntent();
            String post_details = intent.getStringExtra("post_value");
            tag = intent.getStringExtra("tag");
            jsonObject = new JSONObject(post_details);
        } catch (Exception e) {
            jsonObject = null;
        }

        if (jsonObject != null) { // Check jsonObject Not Empty

            if (tag.equalsIgnoreCase("past_trips")) {
                btnCancelRide.setVisibility(View.GONE);
                lnrComments.setVisibility(View.VISIBLE);
                lnrUpcomingLayout.setVisibility(View.GONE);
                getRequestDetails();
                lblTitle.setText(R.string.Past_Trips);
            } else {
                lnrUpcomingLayout.setVisibility(View.VISIBLE);
                btnViewInvoice.setVisibility(View.GONE);
                btnCancelRide.setVisibility(View.VISIBLE);
                lnrComments.setVisibility(View.GONE);
                getUpcomingDetails();
                lblTitle.setText(R.string.Upcoming_Trips);
            }
        }

        // Clicked Profile LinearLayout
        profileLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HistoryDetails.this, ShowProfile.class);
                intent.putExtra("driver", driver);
            }
        });

        // Clicked BackArrow
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditTime();
            }


        });
    }

    private void actions() {
        editTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTimeDialog();
            }
        });
        editDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDateDialog();
            }
        });
    }

    private void openTimeDialog() {
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(HistoryDetails.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                tripTime.setText(selectedHour + ":" + selectedMinute);
            }
        }, hour, minute, true);//Yes 24 hour time
        mTimePicker.setTitle((R.string.select_time));
        mTimePicker.show();
    }

    private void openDateDialog() {

        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR); // current year
        int mMonth = c.get(Calendar.MONTH); // current month
        int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
        // date picker dialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(activity,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        // set day of month , month and year value in the edit text
                        String choosedMonth = "";
                        String choosedDate = "";
                        String choosedDateFormat = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
                        try {
                            choosedMonth = new Utilities().getMonth(choosedDateFormat);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        if (dayOfMonth < 10) {
                            choosedDate = "0" + dayOfMonth;
                        } else {
                            choosedDate = "" + dayOfMonth;
                        }
                        if (Utilities.isAfterToday(year, monthOfYear, dayOfMonth))
                            tripDate.setText(choosedDate + " " + choosedMonth + " " + year);
                        else
                            Toast.makeText(getApplication(), (R.string.wrong_schedule), Toast.LENGTH_SHORT).show();
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.getDatePicker().setMaxDate((System.currentTimeMillis() - 1000) + (1000 * 60 * 60 * 24 * 7));
        datePickerDialog.show();


    }

    // Initialize Elements & Clicked
    public void findViewByIdAndInitialize() {

        activity = HistoryDetails.this;
        context = HistoryDetails.this;
        helper = new ConnectionHelper(activity);
        isInternet = helper.isConnectingToInternet();

        parentLayout = findViewById(R.id.parentLayout);
        profileLayout = findViewById(R.id.profile_detail_layout);
        lnrInvoice = findViewById(R.id.lnrInvoice);
        lnrInvoiceSub = findViewById(R.id.lnrInvoiceSub);
        parentLayout.setVisibility(View.GONE);
        backArrow = findViewById(R.id.backArrow);
        tripAmount = findViewById(R.id.tripAmount);
        tripDate = findViewById(R.id.tripDate);
        tripTime = findViewById(R.id.tripTime);
        paymentType = findViewById(R.id.paymentType);
        booking_id = findViewById(R.id.booking_id);
        paymentTypeImg = findViewById(R.id.paymentTypeImg);
        tripImg = findViewById(R.id.tripImg);
        tripComments = findViewById(R.id.tripComments);
        tripSource = findViewById(R.id.tripSource);
        tripDestination = findViewById(R.id.tripDestination);
        lblBookingID = findViewById(R.id.lblBookingID);
        lblBasePrice = findViewById(R.id.lblBasePrice);
        lblTaxPrice = findViewById(R.id.lblTaxPrice);
        lblDistancePrice = findViewById(R.id.lblDistancePrice);
        lblTotalPrice = findViewById(R.id.lblTotalPrice);
        lblTitle = findViewById(R.id.lblTitle);
        btnCancelRide = findViewById(R.id.btnCancelRide);
        sourceAndDestinationLayout = findViewById(R.id.sourceAndDestinationLayout);
        lnrComments = findViewById(R.id.lnrComments);
        viewLayout = findViewById(R.id.ViewLayout);
        editDate = findViewById(R.id.edit_trip_date);
        editTime = findViewById(R.id.edit_trip_time);

        lnrUpcomingLayout = findViewById(R.id.lnrUpcomingLayout);
        btnViewInvoice = findViewById(R.id.btnViewInvoice);
        btnConfirm = findViewById(R.id.btnConfirmRequest);
        btnEdit = findViewById(R.id.btnEdit);

        // Clicked btnCancelRide
        btnCancelRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setIcon(R.mipmap.ic_launcher)
                        .setTitle(R.string.app_name)
                        .setMessage(getString(R.string.cencel_request))
                        .setCancelable(true)
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                                showreasonDialog();
                            }
                        })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        // Clicked Btn ViewInvoice
        btnViewInvoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lnrInvoice.setVisibility(View.VISIBLE);
            }
        });

        // Clicked Invoice Layout
        lnrInvoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lnrInvoice.setVisibility(View.GONE);
            }
        });

        // Clicked Call Phone Derive
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createRequest(tripId);
            }
        });
    }

    // Result Response Permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + driver.getMobile()));
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            startActivity(intent);
        } else {

        }
    }

    // Show Dialog
    private void showreasonDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.cancel_dialog, null);
        final EditText reasonEtxt = (EditText) view.findViewById(R.id.reason_etxt);
        Button submitBtn = (Button) view.findViewById(R.id.submit_btn);
        builder.setIcon(R.mipmap.ic_launcher)
                .setTitle(R.string.app_name)
                .setView(view)
                .setCancelable(true);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reason = reasonEtxt.getText().toString();
                cancelRequest();

            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    // Method Get Request Details
    public void getRequestDetails() {

        loadingDialog = new LoadingDialog(context);
        loadingDialog.setCancelable(false);
        if (loadingDialog != null)
            loadingDialog.show();

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(URLHelper.GET_HISTORY_DETAILS_API + "?request_id=" + jsonObject.optString("id") + "&device_mac=" + Utilities.getMacAddr()
                + "&lang=" + Utilities.getDeviceLocal(), new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                Log.d("wwwwwwwres ",response.toString());
                if (response != null && response.length() > 0) {
                    if (response.optJSONObject(0) != null) {
//                        Picasso.with(HistoryDetails.this).
//                                load().
//                                placeholder(R.drawable.placeholder).
//                                error(R.drawable.placeholder).into(tripImg);
                        Glide.with(getApplicationContext())
                                .load(jsonObject.
                                        optString("static_map"))
                                .into(tripImg);

                        JSONObject providerObj = response.optJSONObject(0).optJSONObject("provider");
                        Log.d("wwwwwwwres provider ",providerObj.toString());

                        if (jsonObject != null) {
                            driver = new Driver();
                            driver.setFname(providerObj.optString("first_name"));
                            driver.setLname(providerObj.optString("last_name"));
                            driver.setMobile(providerObj.optString("mobile"));
                            driver.setEmail(providerObj.optString("email"));
                            driver.setImg(URLHelper.base_pic2+providerObj.optString("avatar"));
                            driver.setRating(providerObj.optString("rating"));
                        }

                        //Booking_id
                        if (response.optJSONObject(0).optJSONObject("payment") != null) {
                          //  holder.booking_id.setText(getString(R.string.booking_his_trip) + "" + jsonArray.optJSONObject(position).optJSONObject("payment").optString("id"));

                            booking_id.setText(getString(R.string.booking_his_trip) + "" + response.optJSONObject(0).optJSONObject("payment").optString("id"));
                          //  lblBookingID.setText(response.optJSONObject(0).optString("booking_id"));


                        }
//                        if (response.optJSONObject(0).optString("booking_id") != null &&
//                                !response.optJSONObject(0).optString("booking_id").equalsIgnoreCase("")) {
//                            booking_id.setText(response.optJSONObject(0).optString("booking_id"));
//                            lblBookingID.setText(response.optJSONObject(0).optString("booking_id"));
//                        }
                        String form;
                        if (tag.equalsIgnoreCase("past_trips")) {
                            form = response.optJSONObject(0).optString("assigned_at");
                        } else {
                            form = response.optJSONObject(0).optString("schedule_at");
                        }
                        if (response.optJSONObject(0).optJSONObject("payment") != null && response.optJSONObject(0).optJSONObject("payment").optString("total") != null &&
                                !response.optJSONObject(0).optJSONObject("payment").optString("total").equalsIgnoreCase("")) {
                            tripAmount.setText(SharedHelper.getKey(context, "currency") + "" + response.optJSONObject(0).optJSONObject("payment").optString("total"));
                            response.optJSONObject(0).optJSONObject("payment");
                            lblBasePrice.setText((SharedHelper.getKey(context, "currency") + ""
                                    + response.optJSONObject(0).optJSONObject("payment").optString("fixed")));
                            lblDistancePrice.setText((SharedHelper.getKey(context, "currency") + ""
                                    + response.optJSONObject(0).optJSONObject("payment").optString("distance")));
                            lblTaxPrice.setText((SharedHelper.getKey(context, "currency") + ""
                                    + response.optJSONObject(0).optJSONObject("payment").optString("tax")));
                            lblTotalPrice.setText((SharedHelper.getKey(context, "currency") + ""
                                    + response.optJSONObject(0).optJSONObject("payment").optString("total" +
                                    "")));
                        } else {
                            tripAmount.setVisibility(View.GONE);
                        }
                        String form2=jsonObject.optString("arrived_at");
                        String date[]=form2.split(" ");
                        try {


                            tripDate.setText(date[0]);
                            tripTime.setText(date[1]);
                            Log.d("zzzzzzzz", "onResponse: "+getDate(form) + "th " + getMonth(form) + " " + getYear(form));

                        } catch (ParseException e) {
                            Log.d("zzzzzzzz", e.getMessage()+"  is   "+form2);

                        }

                        paymentType.setText(response.optJSONObject(0).optString("payment_mode"));
                        if (response.optJSONObject(0).optString("payment_mode").equalsIgnoreCase("CASH")) {
                            paymentTypeImg.setImageResource(R.drawable.money_icon);
                        } else {
                            paymentTypeImg.setImageResource(R.drawable.visa);
                        }
                        if (response.optJSONObject(0).optJSONObject("rating") != null &&
                                !response.optJSONObject(0).optJSONObject("rating").optString("provider_comment").equalsIgnoreCase("")) {
                            tripComments.setText(response.optJSONObject(0).optJSONObject("rating").optString("provider_comment", ""));
                        } else {
                            tripComments.setText(getString(R.string.no_comments));
                        }
                        if (response.optJSONObject(0).optString("s_address") == null || response.optJSONObject(0).optString("d_address") == null || response.optJSONObject(0).optString("d_address").equals("") || response.optJSONObject(0).optString("s_address").equals("")) {
                            sourceAndDestinationLayout.setVisibility(View.GONE);
                            viewLayout.setVisibility(View.GONE);
                        } else {
                            tripSource.setText(response.optJSONObject(0).optString("s_address"));
                            tripDestination.setText(response.optJSONObject(0).optString("d_address"));
                        }

                    }
                }
                if ((loadingDialog != null) && (loadingDialog.isShowing()))
                    loadingDialog.dismiss();
                parentLayout.setVisibility(View.VISIBLE);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if ((loadingDialog != null) && (loadingDialog.isShowing()))
                    loadingDialog.dismiss();
                String json = null;
                String Message;
                NetworkResponse response = error.networkResponse;
                if (response != null && response.data != null) {

                    try {
                        JSONObject errorObj = new JSONObject(new String(response.data));

                        if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                            try {
                                displayMessage(errorObj.optString("message"));
                            } catch (Exception e) {
                                displayMessage(getString(R.string.something_went_wrong));
                            }

                        } else if (response.statusCode == 401) {
                            refreshAccessToken("PAST_TRIPS");
                        } else if (response.statusCode == 422) {

                            json = trimMessage(new String(response.data));
                            if (json != "" && json != null) {
                                displayMessage(json);
                            } else {
                                displayMessage(getString(R.string.please_try_again));
                            }
                        } else if (response.statusCode == 503) {
                            displayMessage(getString(R.string.server_down));

                        } else {
                            displayMessage(getString(R.string.please_try_again));

                        }

                    } catch (Exception e) {
                        displayMessage(getString(R.string.something_went_wrong));

                    }

                } else {
                    displayMessage(getString(R.string.please_try_again));

                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "" + SharedHelper.getKey(context, "token_type") + " " + SharedHelper.getKey(context, "access_token"));
                headers.put("lang", Utilities.getDeviceLocal());
                return headers;
            }
        };

        App.getInstance().addToRequestQueue(jsonArrayRequest);
    }

    // Method Get UpComing Details
    public void getUpcomingDetails() {

        loadingDialog = new LoadingDialog(context);
        loadingDialog.setCancelable(false);
        if (loadingDialog != null)
            loadingDialog.show();

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(URLHelper.UPCOMING_TRIP_DETAILS + "?request_id=" + jsonObject.optString("id")
                + "&device_mac=" + Utilities.getMacAddr()
                + "&lang=" + Utilities.getDeviceLocal(), new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                if (response != null && response.length() > 0) {
                    if (response.optJSONObject(0) != null) {
                        try {
                            tripId = Integer.parseInt(response.optJSONObject(0).getString("id"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Picasso.with(activity).load(response.optJSONObject(0).optString("static_map")).placeholder(R.drawable.placeholder).error(R.drawable.placeholder).into(tripImg);
                        paymentType.setText(response.optJSONObject(0).optString("payment_mode"));
                        String form = response.optJSONObject(0).optString("schedule_at");
                        JSONObject providerObj = response.optJSONObject(0).optJSONObject("provider");
                        if (response.optJSONObject(0).optString("booking_id") != null &&
                                !response.optJSONObject(0).optString("booking_id").equalsIgnoreCase("")) {
                            booking_id.setText(response.optJSONObject(0).optString("booking_id"));
                        }
                        if (providerObj != null) {
                            driver = new Driver();
                            driver.setFname(providerObj.optString("first_name"));
                            driver.setLname(providerObj.optString("last_name"));
                            driver.setMobile(providerObj.optString("mobile"));
                            driver.setEmail(providerObj.optString("email"));
                            driver.setImg(providerObj.optString("avatar"));
                            driver.setRating(providerObj.optString("rating"));
                        }
                        try {
                            tripDate.setText(getDate(form) + "th " + getMonth(form) + " " + getYear(form));
                            tripTime.setText(getTime(form));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        if (response.optJSONObject(0).optString("payment_mode").equalsIgnoreCase("CASH")) {
                            paymentTypeImg.setImageResource(R.drawable.money_icon);
                        } else {
                            paymentTypeImg.setImageResource(R.drawable.visa);
                        }
                        try {
                        } catch (Exception e) {

                        }

                        if (response.optJSONObject(0).optString("s_address") == null || response.optJSONObject(0).optString("d_address") == null || response.optJSONObject(0).optString("d_address").equals("") || response.optJSONObject(0).optString("s_address").equals("")) {
                            sourceAndDestinationLayout.setVisibility(View.GONE);
                            viewLayout.setVisibility(View.GONE);
                        } else {
                            tripSource.setText(response.optJSONObject(0).optString("s_address"));
                            tripDestination.setText(response.optJSONObject(0).optString("d_address"));
                        }

                        try {
                            JSONObject serviceObj = response.optJSONObject(0).optJSONObject("service_type");
                            if (serviceObj != null) {
                                if (tag.equalsIgnoreCase("past_trips")) {
                                    tripAmount.setText(SharedHelper.getKey(context, "currency") + serviceObj.optString("price"));
                                } else {
                                    tripAmount.setVisibility(View.GONE);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }
                    if ((loadingDialog != null) && (loadingDialog.isShowing()))
                        loadingDialog.dismiss();
                    parentLayout.setVisibility(View.VISIBLE);

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if ((loadingDialog != null) && (loadingDialog.isShowing()))
                    loadingDialog.dismiss();
                String json = null;
                String Message;
                NetworkResponse response = error.networkResponse;
                if (response != null && response.data != null) {

                    try {
                        JSONObject errorObj = new JSONObject(new String(response.data));

                        if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                            try {
                                displayMessage(errorObj.optString("message"));
                            } catch (Exception e) {
                                displayMessage(getString(R.string.something_went_wrong));
                            }

                        } else if (response.statusCode == 401) {
                            refreshAccessToken("UPCOMING_TRIPS");
                        } else if (response.statusCode == 422) {

                            json = trimMessage(new String(response.data));
                            if (json != "" && json != null) {
                                displayMessage(json);
                            } else {
                                displayMessage(getString(R.string.please_try_again));
                            }
                        } else if (response.statusCode == 503) {
                            displayMessage(getString(R.string.server_down));

                        } else {
                            displayMessage(getString(R.string.please_try_again));

                        }

                    } catch (Exception e) {
                        displayMessage(getString(R.string.something_went_wrong));

                    }

                } else {
                    displayMessage(getString(R.string.please_try_again));
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "" + SharedHelper.getKey(context, "token_type") + " " + SharedHelper.getKey(context, "access_token"));
                return headers;
            }
        };

        App.getInstance().addToRequestQueue(jsonArrayRequest);
    }

    // Method Refresh Access Token
    private void refreshAccessToken(final String tag) {

        JSONObject object = new JSONObject();
        try {
            object.put("grant_type", "refresh_token");
            object.put("client_id", URLHelper.client_id);
            object.put("client_secret", URLHelper.client_secret);
            object.put("refresh_token", SharedHelper.getKey(context, "refresh_token"));
            object.put("scope", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.login, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                SharedHelper.putKey(context, "access_token", response.optString("access_token"));
                SharedHelper.putKey(context, "refresh_token", response.optString("refresh_token"));
                SharedHelper.putKey(context, "token_type", response.optString("token_type"));
                if (tag.equalsIgnoreCase("PAST_TRIPS")) {
                    getRequestDetails();
                } else if (tag.equalsIgnoreCase("UPCOMING_TRIPS")) {
                    getUpcomingDetails();
                } else if (tag.equalsIgnoreCase("CANCEL_REQUEST")) {
                    cancelRequest();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String json = null;
                String Message;
                NetworkResponse response = error.networkResponse;

                if (response != null && response.data != null) {
                    SharedHelper.putKey(context, "loggedIn", getString(R.string.False));
                    GoToBeginActivity();
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                return headers;
            }
        };

        App.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    // Display SnackBar Message
    public void displayMessage(String toastString) {
        //Toast.makeText(activity, toastString, Toast.LENGTH_SHORT).show();
    }

    // Method Go To Begin Activity
    public void GoToBeginActivity() {
        Intent mainIntent = new Intent(activity, BeginScreen.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
        activity.finish();
    }

    // Method OnBackPressed
    @Override
    public void onBackPressed() {
        if (lnrInvoice.getVisibility() == View.VISIBLE) {
            lnrInvoice.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
        }
    }

    // Method Getting Cancel Requests
    public void cancelRequest() {
        loadingDialog = new LoadingDialog(context);
        loadingDialog.setCancelable(false);
        if (loadingDialog != null)
            loadingDialog.show();
        JSONObject object = new JSONObject();
        try {
            object.put("request_id", jsonObject.optString("id"));
            object.put("trip_id", jsonObject.optString("id"));
            object.put("cancel_reason", reason);
            object.put("device_mac", Utilities.getMacAddr());

        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.Delete_Schedule, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if ((loadingDialog != null) && (loadingDialog.isShowing()))
                    loadingDialog.dismiss();
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if ((loadingDialog != null) && (loadingDialog.isShowing()))
                    loadingDialog.dismiss();
                String json = null;
                String Message;
                NetworkResponse response = error.networkResponse;
                if (response != null && response.data != null) {

                    try {
                        JSONObject errorObj = new JSONObject(new String(response.data));

                        if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                            try {
                                displayMessage(errorObj.optString("message"));
                            } catch (Exception e) {
                                displayMessage(getString(R.string.something_went_wrong));
                            }
                        } else if (response.statusCode == 401) {
                            refreshAccessToken("CANCEL_REQUEST");
                        } else if (response.statusCode == 422) {

                            json = trimMessage(new String(response.data));
                            if (json != "" && json != null) {
                                displayMessage(json);
                            } else {
                                displayMessage(getString(R.string.please_try_again));
                            }
                        } else if (response.statusCode == 503) {
                            displayMessage(getString(R.string.server_down));
                        } else {
                            displayMessage(getString(R.string.please_try_again));
                        }

                    } catch (Exception e) {
                        displayMessage(getString(R.string.something_went_wrong));
                    }

                } else {
                    displayMessage(getString(R.string.please_try_again));
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "" + SharedHelper.getKey(context, "token_type") + " " + SharedHelper.getKey(context, "access_token"));
                headers.put("lang", Utilities.getDeviceLocal());
                return headers;
            }
        };

        App.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    public void EditTime() {

        loadingDialog = new LoadingDialog(context);
        loadingDialog.setCancelable(false);
        if (loadingDialog != null)
            loadingDialog.show();
        JSONObject object = new JSONObject();
        try {
            object.put("trip_id", jsonObject.optString("id"));
            object.put("schedule_date", tripDate.getText().toString());
            object.put("schedule_time", tripTime.getText().toString());
            object.put("device_mac", Utilities.getMacAddr());

        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.Edit_Schedule, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if ((loadingDialog != null) && (loadingDialog.isShowing()))
                    loadingDialog.dismiss();
//                try {
//                   // Toast.makeText(activity, response.getString("message"), Toast.LENGTH_SHORT).show();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if ((loadingDialog != null) && (loadingDialog.isShowing()))
                    loadingDialog.dismiss();
                String json = null;
                String Message;
                NetworkResponse response = error.networkResponse;
                if (response != null && response.data != null) {

                    try {
                        JSONObject errorObj = new JSONObject(new String(response.data));

                        if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                            try {
                                displayMessage(errorObj.optString("message"));
                            } catch (Exception e) {
                                displayMessage(getString(R.string.something_went_wrong));
                            }
                        } else if (response.statusCode == 401) {
                            Utilities.goToLogin(activity);
                        } else if (response.statusCode == 422) {

                            json = trimMessage(new String(response.data));
                            if (json != "" && json != null) {
                                displayMessage(json);
                            } else {
                                displayMessage(getString(R.string.please_try_again));
                            }
                        } else if (response.statusCode == 503) {
                            displayMessage(getString(R.string.server_down));
                        } else {
                            displayMessage(getString(R.string.please_try_again));
                        }

                    } catch (Exception e) {
                        displayMessage(getString(R.string.something_went_wrong));
                    }

                } else {
                    displayMessage(getString(R.string.please_try_again));
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "" + SharedHelper.getKey(context, "token_type") + " " + SharedHelper.getKey(context, "access_token"));
                headers.put("lang", Utilities.getDeviceLocal());
                return headers;
            }
        };

        App.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    // Method Getting Month Name Requests
    private String getMonth(String date) throws ParseException {
        Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        String monthName = new SimpleDateFormat("MMM").format(cal.getTime());
        return monthName;
    }

    // Method Getting Name Day
    private String getDate(String date) throws ParseException {
        Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        String dateName = new SimpleDateFormat("dd").format(cal.getTime());
        return dateName;
    }

    // Method Getting Name Year
    private String getYear(String date) throws ParseException {
        Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        String yearName = new SimpleDateFormat("yyyy").format(cal.getTime());
        return yearName;
    }

    // Method Getting Time
    private String getTime(String date) throws ParseException {
        Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        String timeName = new SimpleDateFormat("hh:mm a").format(cal.getTime());
        return timeName;
    }

    private void createRequest(final int tripId) {

        JSONObject object = new JSONObject();
        try {
            object.put("id", tripId);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        App.getInstance().cancelRequestInQueue("send_request");
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.Send_Schedule, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                JSONObject object = new JSONObject();
                //Toast.makeText(activity, SharedHelper.getKey(context, "rating"), Toast.LENGTH_SHORT).show();
                try {
                    object.put("s_latitude", response.getJSONObject("data").getString("s_latitude"));
                    object.put("s_latitude", response.getJSONObject("data").getString("s_latitude"));
                    object.put("s_longitude", response.getJSONObject("data").getString("s_latitude"));
                    object.put("d_latitude", response.getJSONObject("data").getString("s_latitude"));
                    object.put("d_longitude", response.getJSONObject("data").getString("s_latitude"));
                    object.put("first_name", SharedHelper.getKey(context, "first_name"));
                    object.put("last_name", SharedHelper.getKey(context, "last_name"));
                    object.put("email", SharedHelper.getKey(context, "email"));
                    object.put("picture", SharedHelper.getKey(context, "picture"));
                    object.put("rating", SharedHelper.getKey(context, "rating"));
                    object.put("mobile", SharedHelper.getKey(context, "mobile"));
                    object.put("status", "SEARCHING");
                    object.put("booking_id", "");
                    try {
                        object.put("s_address", response.getJSONObject("data").getString("s_latitude"));
                        object.put("d_address", response.getJSONObject("data").getString("s_latitude"));
                    } catch (Exception e) {

                    }

                    object.put("id", SharedHelper.getKey(getApplication(), "id"));
                    object.put("service_type", SharedHelper.getKey(context, "service_type"));
                    object.put("distance", SharedHelper.getKey(context, "distance"));


                    if (SharedHelper.getKey(context, "payment_mode").equals("CASH")) {
                        object.put("payment_mode", SharedHelper.getKey(context, "payment_mode"));
                    } else {
                        object.put("payment_mode", SharedHelper.getKey(context, "payment_mode"));
                        object.put("card_id", SharedHelper.getKey(context, "card_id"));
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent(getApplicationContext(), SocketService.class);
                intent.setAction(SocketService.ACTION_START_FOREGROUND_SERVICE);
                startService(intent);

                SendRequestToSocket(tripId, object);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if ((loadingDialog != null) && (loadingDialog.isShowing()))
                    loadingDialog.dismiss();
            }

        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "" + SharedHelper.getKey(context, "token_type") + " " + SharedHelper.getKey(context, "access_token"));
                return headers;
            }
        };

        App.getInstance().addToRequestQueue(jsonObjectRequest);

    }

    private void SendRequestToSocket(final int tripId, final JSONObject object) {

//      put request id in json
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (object.length() > 0) {

                    // Sending All Value In Socket io
                    JSONObject requestObj = new JSONObject();
                    try {
                        requestObj.put("request", object);
                        requestObj.put("UserId", SharedHelper.getKey(getApplication(), "id"));
                        requestObj.put("Trip_id", tripId);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (SocketService.socket != null) {
                        SocketService.socket.emit("request_range", requestObj);
                        //Toast.makeText(getApplication(), "Done Send", Toast.LENGTH_SHORT).show();
                        loadingDialog.dismiss();
                    } else {
                        //Toast.makeText(getApplication(), "Don't Send", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }, 5000);

    }

    private void validatExpiredTime(String scheduleDate) {
        int x = hoursAgo(scheduleDate);

        lnrUpcomingLayout.setVisibility(View.GONE);
        btnViewInvoice.setVisibility(View.VISIBLE);

    }

    public static int hoursAgo(String datetime) {
        Date date = null; // Parse into Date object
        try {
            date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.ENGLISH).parse(datetime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date now = Calendar.getInstance().getTime(); // Get time now
        long differenceInMillis = now.getTime() - date.getTime();
        long differenceInHours = (differenceInMillis) / 1000L / 60L / 60L; // Divide by millis/sec, secs/min, mins/hr
        return (int) differenceInHours;
    }

}
