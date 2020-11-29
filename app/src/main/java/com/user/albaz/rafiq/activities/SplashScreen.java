package com.user.albaz.rafiq.activities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.Settings;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.user.albaz.rafiq.App;
import com.user.albaz.rafiq.R;
import com.user.albaz.rafiq.helper.ConnectionHelper;
import com.user.albaz.rafiq.helper.SharedHelper;
import com.user.albaz.rafiq.helper.URLHelper;
import com.user.albaz.rafiq.utils.Utilities;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.user.albaz.rafiq.App.trimMessage;

public class SplashScreen extends AppCompatActivity {

    public Activity activity = SplashScreen.this;
    public Context context = SplashScreen.this;
    ConnectionHelper helper;
    Boolean isInternet;
    String device_token, device_UDID;
    Handler handleCheckStatus;
    int retryCount = 0;
    AlertDialog alert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        helper = new ConnectionHelper(context);
        isInternet = helper.isConnectingToInternet(); // check the internet connection


        //check status every 3 sec
        handleCheckStatus = new Handler();

        // Check SDK Environment & Set Attributes
        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        //GoToMainActivity();

        /**
         ***************** to go online you should uncomment this ******************
         * and comment GoToMainActivity() directly in onCreate**/
        // Check Status LogIn Or Not After 3Second
        handleCheckStatus.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (helper.isConnectingToInternet()) {
                    if (SharedHelper.getKey(context, "loggedIn").equalsIgnoreCase(getString(R.string.True))) {
                        // Invoke Method To Retrieve All Data App in SharedPrefaced
                        getDetails();
                        GetToken();
//                        getProfile();
                    } else {
                        GoToBeginActivity();
                        // Invoke Method To Retrieve All Data App in SharedPrefaced
                        getDetails();
                    }
                    if (alert != null && alert.isShowing()) {
                        alert.dismiss();
                    }
                } else {
                    showDialog();
                    handleCheckStatus.postDelayed(this, 3000);
                }
            }
        }, 3000);
        // ************/

    }

    // Method Getting Profile
    public void getProfile() {
        retryCount++;
        JSONObject object = new JSONObject();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URLHelper.UserProfile
                + "?device_type=android&device_id=" + device_UDID
                + "&device_token=" + device_token
                + "&device_mac=" + Utilities.getMacAddr()
                + "&lang=" + Utilities.getDeviceLocal(), object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.v("aaaaaaaaaaaaaaaaaaaaa", "getProfile onResponse");
                    SharedHelper.putKey(context, "id", response.optString("id"));
                    SharedHelper.putKey(context, "first_name", response.optString("first_name"));
                    SharedHelper.putKey(context, "last_name", response.optString("last_name"));
                    SharedHelper.putKey(context, "email", response.optString("email"));
                    SharedHelper.putKey(context, "rating", response.optString("rating"));
                    Log.v("aaaaaaaaaaaaaaaaaaaaa", "getProfile onResponse");
                    if (SharedHelper.getKey(context, "login_by").equals("facebook") || SharedHelper.getKey(context, "login_by").equals("google")) {
                        if (response.optString("picture").startsWith("http"))
                            SharedHelper.putKey(context, "picture", response.optString("picture"));
                    } else {
                        SharedHelper.putKey(context, "picture", URLHelper.base_pic + response.optString("picture"));
                    }
                    // Toast.makeText(activity, response.optString("booking_id"), Toast.LENGTH_SHORT).show();
                    SharedHelper.putKey(context, "gender", response.optString("gender"));
                    SharedHelper.putKey(context, "mobile", response.optString("mobile").substring(2));
                    SharedHelper.putKey(context, "wallet_balance", response.optString("wallet_balance"));
                    SharedHelper.putKey(context, "payment_mode", response.optString("payment_mode"));
                    if (!response.optString("currency").equalsIgnoreCase("") && response.optString("currency") != null)
                        SharedHelper.putKey(context, "currency", response.optString("currency"));
                    else
                        SharedHelper.putKey(context, "currency", "$");
                    SharedHelper.putKey(context, "sos", response.optString("sos"));
                    SharedHelper.putKey(context, "loggedIn", getString(R.string.True));
                    GoToMainActivity();
                } catch (Exception e) {
                    goToBeginActivity();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                goToBeginActivity();
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
        jsonObjectRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 10;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });

        App.getInstance().addToRequestQueue(jsonObjectRequest);

    }

    public void getProfile1() {

        retryCount++;
        JSONObject object = new JSONObject();
        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(URLHelper.UserProfile
                + "?device_type=android&device_id=" + device_UDID
                + "&device_token=" + device_token
                + "&device_mac=" + Utilities.getMacAddr()
                + "&lang=" + Utilities.getDeviceLocal(), new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    SharedHelper.putKey(context, "id", response.getJSONObject(0).optString("id"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                GoToMainActivity();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (false) {
                    getProfile();
                } else {
                    GoToBeginActivity();
                }
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
                        } else if (response.statusCode == 422) {

                            json = trimMessage(new String(response.data));
                            if (json != "" && json != null) {
                                displayMessage(json);
                            } else {
                                displayMessage(getString(R.string.please_try_again));
                            }

                        } else if (response.statusCode == 503) {
                            displayMessage(getString(R.string.server_down));
                        }
                    } catch (Exception e) {
                    }

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
        jsonObjectRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });

        App.getInstance().addToRequestQueue(jsonObjectRequest);

    }

    // Method Invoked When Destroy App
    @Override
    protected void onDestroy() {
        handleCheckStatus.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    // Method Refresh Access Token
    private void refreshAccessToken() {

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
                getProfile();
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
                } else {
                    if (error instanceof NoConnectionError) {
                        displayMessage(getString(R.string.oops_connect_your_internet));
                    } else if (error instanceof NetworkError) {
                        displayMessage(getString(R.string.oops_connect_your_internet));
                    } else if (error instanceof TimeoutError) {
                        refreshAccessToken();
                    }
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

    // Method Invoked When App Pause
    @Override
    protected void onPause() {
        super.onPause();
    }

    // Method GoToMainActivity
    public void GoToMainActivity() {
        Intent mainIntent = new Intent(activity, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
        activity.finish();
    }

    // Method GoToBeginActivity
    public void GoToBeginActivity() {
        Intent mainIntent = new Intent(activity, BeginScreen.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
        activity.finish();
    }

    //  Show Toast Message
    public void displayMessage(String toastString) {
        //  Toast.makeText(activity, toastString, Toast.LENGTH_SHORT).show();
    }

    // Method Getting Token
    public void GetToken() {
        try {
            if (!SharedHelper.getKey(context, "device_token").equals("") && SharedHelper.getKey(context, "device_token") != null) {
                device_token = SharedHelper.getKey(context, "device_token");
            } else {
                device_token = "COULD NOT GET FCM TOKEN";
            }
        } catch (Exception e) {
            device_token = "COULD NOT GET FCM TOKEN";
        }

        try {
            device_UDID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        } catch (Exception e) {
            device_UDID = "COULD NOT GET UDID";
            e.printStackTrace();
        }
    }

    // Alert When No have Internet
    private void showDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.connect_to_network))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.connect_to_internet), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new Intent(Settings.ACTION_SETTINGS));
                    }
                })
                .setNegativeButton(getString(R.string.quit), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                });
        if (alert == null) {
            alert = builder.create();

            checkLogin();
        }
    }

    // Get All Details App And Profile User
    void getDetails() {
        JSONObject object = new JSONObject();
        JsonObjectRequest jsonObjectRequest =
                new JsonObjectRequest(Request.Method.GET, URLHelper.appDetails, object, new Response.Listener<JSONObject>() {
                    String App_Name = null,
                            App_Icon = null,
                            App_Logo = null,
                            App_Splash = null,
                            App_Status = null,
                            App_Msg = null,
                            Phone_Number = null,
                            Email = null,
                            Interval_Time = null;

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            App_Name = response.getString("App_Name");
                            App_Icon = response.getString("App_Icon");
                            App_Logo = response.getString("App_Logo");
                            App_Splash = response.getString("App_Splash");
                            App_Status = response.getString("App_Status");
                            App_Msg = response.getString("App_Msg");
                            Phone_Number = response.getString("Phone_Number");
                            Email = response.getString("Email");
                            Interval_Time = response.getString("Interval_Time");
                            // Set Currency & SOS
                            if (!response.optString("Currency").equalsIgnoreCase("") && response.optString("Currency") != null)
                                SharedHelper.putKey(context, "currency", response.optString("Currency"));
                            else
                                SharedHelper.putKey(context, "currency", "$");
                            SharedHelper.putKey(context, "sos", response.optString("Sos"));

                            SharedPreferences sharedPreferences = getSharedPreferences("app_details", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("App_Name", App_Name);
                            editor.putString("App_Icon", App_Icon);
                            editor.putString("App_Logo", App_Logo);
                            editor.putString("App_Splash", App_Splash);
                            editor.putString("App_Status", App_Status);
                            editor.putString("App_Msg", App_Msg);
                            editor.putString("Phone_Number", Phone_Number);
                            editor.putString("Email", Email);
                            editor.putString("Interval_Time", Interval_Time);
                            editor.apply();
                            getProfile();

                            if (App_Status.contains("1")) {

                                if (App_Msg != null || !App_Msg.contains("")) {
                                }
                                return;
                            }
                            else goToBeginActivity();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            //    Toast.makeText(activity, "error:" + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        displayMessage(getString(R.string.please_try_again));
                        checkLogin();
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<String, String>();
                        headers.put("X-Requested-With", "XMLHttpRequest");
                        return headers;
                    }
                };
        jsonObjectRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 5000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 10;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
        App.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    private void checkLogin() {
        String s = SharedHelper.getKey(getApplicationContext(), "access_token");
        if (s.equals("")) {
            goToBeginActivity();
        } else {
            goToMainActivity();
        }
        if (alert != null && alert.isShowing()) {
        }
    }

    public final void goToMainActivity() {
        Intent mainIntent = new Intent(SplashScreen.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mainIntent.putExtra("connect", false);
        startActivity(mainIntent);
        this.finish();
    }

    public final void goToBeginActivity() {
        SharedHelper.putKey(getApplicationContext(), "loggedIn", getString(R.string.False));
        Intent mainIntent = new Intent(this, BeginScreen.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
        finish();
    }
}
