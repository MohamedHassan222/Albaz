package com.user.albaz.rafiq.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;

import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.user.albaz.rafiq.App;
import com.user.albaz.rafiq.R;
import com.user.albaz.rafiq.base.ServiceActivity;
import com.user.albaz.rafiq.helper.ConnectionHelper;
import com.user.albaz.rafiq.helper.LoadingDialog;
import com.user.albaz.rafiq.helper.SharedHelper;
import com.user.albaz.rafiq.helper.URLHelper;
import com.user.albaz.rafiq.utils.MyTextView;
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
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.user.albaz.rafiq.App.trimMessage;


public class ActivityPassword extends ServiceActivity {

    private static String TAG = "ACTIVITY_PASSWORD";

    public Context context = ActivityPassword.this;
    public Activity activity = ActivityPassword.this;
    ConnectionHelper helper;
    Boolean isInternet;
    ImageView backArrow;
    FloatingActionButton nextICON;
    EditText password;
    MyTextView register, forgetPassword;
    LoadingDialog loadingDialog;

    String device_token, device_UDID;
    Utilities utils = new Utilities();
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        // Invoke Defined
        findViewByIdandInit();
        GetToken();

        // Check SDK
        if (Build.VERSION.SDK_INT > 15) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        // Clicked Next Icon
        nextICON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (password.getText().toString().length() == 0) {
                    displayMessage(getString(R.string.password_validation));
                } else if (password.length() < 6) {
                    displayMessage(getString(R.string.password_size));
                } else {
                    SharedHelper.putKey(context, "password", password.getText().toString());
                    signIn();
                }

            }
        });

        // Clicked Back Icon
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        // Clicked EditText Registration
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedHelper.putKey(context, "password", "");
                Intent mainIntent = new Intent(activity, RegisterActivity.class);
                startActivity(mainIntent);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        });

        // Clicked EditText Forget Password
        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedHelper.putKey(context, "password", "");
                Intent mainIntent = new Intent(activity, ForgetPassword.class);
                startActivity(mainIntent);
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        if ((loadingDialog != null) && loadingDialog.isShowing())
            loadingDialog.dismiss();
        loadingDialog = null;
    }

    // Method SignIn When Clicked Next Btn
    public void signIn1() {

        if (!hasInternet()) { // Check Internet
            displaySnackbar(getString(R.string.oops_connect_your_internet));
            return;
        }
        showLoading();
        JSONObject object = new JSONObject();
        try {
            object.put("lang", Utilities.getDeviceLocal());
            object.put("device_mac", Utilities.getMacAddr());
            object.put("grant_type", "password");
            object.put("client_id", URLHelper.client_id);
            object.put("client_secret", URLHelper.client_secret);
            object.put("username", SharedHelper.getKey(context, "mobile"));
            object.put("password", SharedHelper.getKey(context, "password"));
            object.put("device_type", "android");
            object.put("scope", "*");
            object.put("device_id", getDeviceUDID());
            object.put("device_token", getDeviceToken());

            if (SharedHelper.getKey(ActivityPassword.this, "login_by").equals("facebook") || SharedHelper.getKey(ActivityPassword.this, "login_by").equals("google")) { // If Login With Social
                object.put("social_unique_id", SharedHelper.getKey(ActivityPassword.this, "id_social"));
                object.put("login_by", SharedHelper.getKey(ActivityPassword.this, "login_by"));
            } else {
                object.put("login_by", "manual");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.login, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                dismissLoading();
                String token = response.optString("accesse_token");
                //Toast.makeText(context, response.optString("access_token"), Toast.LENGTH_SHORT).show();

                String client_id = response.optString("id");
                SharedHelper.putKey(context, "access_token", response.optString("access_token"));
                SharedHelper.putKey(context, "token_type", "Bearer");
                SharedHelper.putKey(context ,"client_id",client_id);


                getProfile(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissLoading();
                String json = null;
                //Toast.makeText(context, "Error" + error.getMessage(), Toast.LENGTH_SHORT).show();
                NetworkResponse response = error.networkResponse;
                if (response != null && response.data != null) {
                    try {
                        JSONObject errorObj = new JSONObject(new String(response.data));

                        if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                            try {
                                displaySnackbar(errorObj.optString("message"));
                            } catch (Exception e) {
                                displaySnackbar(getString(R.string.something_went_wrong));
                            }
                        } else if (response.statusCode == 401) {
                            try {
                                if (errorObj.optString("message").equalsIgnoreCase("invalid_token")) {
                                    //Call Refresh token
                                } else {
                                    displaySnackbar(errorObj.optString("message"));
                                }
                            } catch (Exception e) {
                                displaySnackbar(getString(R.string.something_went_wrong));
                            }

                        } else if (response.statusCode == 422) {

                            json = trimMessage(new String(response.data));
                            if (json != "" && json != null) {
                                displaySnackbar(json);
                            } else {
                                displaySnackbar(getString(R.string.please_try_again));
                            }

                        } else {
                            displaySnackbar(getString(R.string.please_try_again));
                        }

                    } catch (Exception e) {
                        displaySnackbar(getString(R.string.something_went_wrong));
                    }

                } else {
                    if (error instanceof NoConnectionError) {
                        displaySnackbar(getString(R.string.oops_connect_your_internet));
                    } else if (error instanceof NetworkError) {
                        displaySnackbar(getString(R.string.oops_connect_your_internet));
                    } else if (error instanceof TimeoutError) {
                        signIn();
                    }
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", SharedHelper.getKey(ActivityPassword.this, "access_token"));
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

    public final void signIn() {
        if (!requestInternet()) {
            return;
        }
        showLoading();
        JSONObject object = new JSONObject();
        try {
            object.put("lang", Utilities.getDeviceLocal());
            object.put("device_mac", Utilities.getMacAddr());
            object.put("grant_type", "password");
            object.put("client_id", URLHelper.client_id);
            object.put("client_secret", URLHelper.client_secret);
            object.put("username", SharedHelper.getKey(context, "mobile"));
            object.put("password", SharedHelper.getKey(context, "password"));
            object.put("device_type", "android");
            object.put("scope", "*");
            object.put("device_id", getDeviceUDID());
            object.put("device_token", getDeviceToken());

            if (SharedHelper.getKey(ActivityPassword.this, "login_by").equals("facebook") || SharedHelper.getKey(ActivityPassword.this, "login_by").equals("google")) { // If Login With Social
                object.put("social_unique_id", SharedHelper.getKey(ActivityPassword.this, "id_social"));
                object.put("login_by", SharedHelper.getKey(ActivityPassword.this, "login_by"));
            } else {
                object.put("login_by", "manual");
            }
        } catch (Exception e) {
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.login, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                dismissLoading();
                String token = response.optString("access_token");
                SharedHelper.putKey(context, "access_token", response.optString("access_token"));
                SharedHelper.putKey(context, "token_type", "Bearer");
                getProfile();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissLoading();
                String json = null;
                String Message;
                NetworkResponse response = error.networkResponse;
                Log("MyTest: " + error);
                Log("MyTestError: " + error.networkResponse);

                if (response != null && response.data != null) {
                    Log("MyTestError1: " + response.statusCode);
                    try {
                        JSONObject errorObj = new JSONObject(new String(response.data));
                        if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                            displaySnackbar(getString(R.string.something_went_wrong));
                        } else if (response.statusCode == 406) {
                            displaySnackbar(errorObj.optString("message"));
                        } else if (response.statusCode == 401) {

                            try {
                                if (errorObj.optString("message").equalsIgnoreCase("invalid_token")) {
                                    //Call Refresh token
                                } else {
                                    displaySnackbar(errorObj.optString("message"));
                                }
                            } catch (Exception e) {
                                displaySnackbar(getString(R.string.something_went_wrong));
                            }
                        } else if (response.statusCode == 422) {
                            json = App.trimMessage(new String(response.data));
                            if (json != "" && json != null) {
                                displaySnackbar(json);
                            } else {
                                displaySnackbar(getString(R.string.please_try_again));
                            }

                        } else {
                            displaySnackbar(getString(R.string.please_try_again));
                        }

                    } catch (Exception e) {
                        displaySnackbar(getString(R.string.something_went_wrong));
                    }

                } else {
                    if (error instanceof NoConnectionError) {
                        displaySnackbar(getString(R.string.oops_connect_your_internet));
                    } else if (error instanceof NetworkError) {
                        displaySnackbar(getString(R.string.oops_connect_your_internet));
                    } else if (error instanceof TimeoutError) {
                        //signIn1();
                        displaySnackbar(getString(R.string.time_amount));
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

    // Invoke Method Get Date Profile After Login
    public void getProfile() {
        if (isInternet) {
            loadingDialog = new LoadingDialog(context);
            loadingDialog.setCancelable(false);
            if (loadingDialog != null)
                loadingDialog.show();
            JSONObject object = new JSONObject();
            try {
                object.put("device_type", "android");
                object.put("device_id", getDeviceUDID());
                object.put("device_id", getDeviceUDID());
                object.put("device_mac", Utilities.getMacAddr());
                object.put("device_token", "Bearer " + getDeviceToken());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URLHelper.UserProfile
                    + "?device_mac=" + Utilities.getMacAddr()
                    + "&lang=" + Utilities.getDeviceLocal(),
                    object, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if ((loadingDialog != null) && loadingDialog.isShowing())
                        loadingDialog.dismiss();
                    SharedHelper.putKey(context, "id", response.optString("id"));
                    SharedHelper.putKey(context, "first_name", response.optString("first_name"));
                    SharedHelper.putKey(context, "last_name", response.optString("last_name"));
                    SharedHelper.putKey(context, "email", response.optString("email"));
                    if (response.optString("picture").startsWith("http"))
                        SharedHelper.putKey(context, "picture", response.optString("picture"));
                    else
                        SharedHelper.putKey(context, "picture", URLHelper.base_pic + response.optString("picture"));

                    SharedHelper.putKey(context, "mobile", response.optString("mobile").substring(2));
                    SharedHelper.putKey(context, "wallet_balance", response.optString("wallet_balance"));
                    SharedHelper.putKey(context, "payment_mode", response.optString("payment_mode"));

                    SharedHelper.putKey(context, "loggedIn", getString(R.string.True));

                    GoToMainActivity(); // Invoke Method Home Map


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if ((loadingDialog != null) && loadingDialog.isShowing())
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
                        if (error instanceof NoConnectionError) {
                            displayMessage(getString(R.string.oops_connect_your_internet));
                        } else if (error instanceof NetworkError) {
                            displayMessage(getString(R.string.oops_connect_your_internet));
                        } else if (error instanceof TimeoutError) {
                            getProfile();
                        }
                    }
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("X-Requested-With", "XMLHttpRequest");
                    headers.put("Authorization", "" + SharedHelper.getKey(context, "token_type") + " "
                            + SharedHelper.getKey(context, "access_token"));
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
        } else {
            displayMessage(getString(R.string.something_went_wrong_net));
        }

    }

    public void getProfile(JSONObject response) {

        if ((loadingDialog != null) && loadingDialog.isShowing())
            loadingDialog.dismiss();
        SharedHelper.putKey(context, "id", response.optString("id"));
        SharedHelper.putKey(context, "first_name", response.optString("first_name"));
        SharedHelper.putKey(context, "last_name", response.optString("last_name"));
        SharedHelper.putKey(context, "email", response.optString("email"));
        if (response.optString("picture").startsWith("http"))
            SharedHelper.putKey(context, "picture", response.optString("picture"));
        else
            SharedHelper.putKey(context, "picture", URLHelper.base_pic + response.optString("picture"));

        SharedHelper.putKey(context, "mobile", response.optString("mobile"));
        SharedHelper.putKey(context, "wallet_balance", response.optString("wallet_balance"));
        SharedHelper.putKey(context, "payment_mode", response.optString("payment_mode"));

        SharedHelper.putKey(context, "loggedIn", getString(R.string.True));

        GoToMainActivity(); // Invoke Method Home Map

    }


    // Initialize Elements
    public void findViewByIdandInit() {
        register = findViewById(R.id.register);
        forgetPassword = findViewById(R.id.forgetPassword);
        password = findViewById(R.id.password);
        nextICON = findViewById(R.id.nextIcon);
        backArrow = findViewById(R.id.backArrow);

        helper = new ConnectionHelper(context); // Set Context To Check
        isInternet = helper.isConnectingToInternet(); // Check Internet

        dialog = new LoadingDialog(ActivityPassword.this);
        dialog.setContentView(R.layout.dialog_loading);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.CENTER);

    }

    // Method Go To Begin Activity
    public void GoToBeginActivity() {
        Intent mainIntent = new Intent(activity, BeginScreen.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
        activity.finish();
    }

    // Display SnackBar Message
    public void displayMessage(String toastString) {
        utils.print("displayMessage", "" + toastString);
        Snackbar.make(getCurrentFocus(), toastString, Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();
    }

    // Method Go To Main Activity
    public void GoToMainActivity() {
        Intent mainIntent = new Intent(activity, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        activity.finish();
    }

    // Method Back Pressed
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
    }

    // Method Get Device Token
    public void GetToken() {
        try {
            if (!SharedHelper.getKey(context, "device_token").equals("") && SharedHelper.getKey(context, "device_token") != null) {
                device_token = SharedHelper.getKey(context, "device_token");
                utils.print(TAG, "GCM Registration Token: " + device_token);
            } else {
                device_token = "COULD NOT GET FCM TOKEN";
                utils.print(TAG, "Failed to complete token refresh: " + device_token);
            }
        } catch (Exception e) {
            device_token = "COULD NOT GET FCM TOKEN";
            utils.print(TAG, "Failed to complete token refresh");
        }

        try {
            device_UDID = android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
            utils.print(TAG, "Device UDID:" + device_UDID);
        } catch (Exception e) {
            device_UDID = "COULD NOT GET UDID";
            e.printStackTrace();
            utils.print(TAG, "Failed to complete device UDID");
        }
    }
}
