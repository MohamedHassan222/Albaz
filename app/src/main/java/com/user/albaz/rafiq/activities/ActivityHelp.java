package com.user.albaz.rafiq.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.user.albaz.rafiq.App;
import com.user.albaz.rafiq.R;
import com.user.albaz.rafiq.base.ServiceActivity;
import com.user.albaz.rafiq.helper.LoadingDialog;
import com.user.albaz.rafiq.helper.SharedHelper;
import com.user.albaz.rafiq.helper.URLHelper;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.user.albaz.rafiq.App.trimMessage;

/* Support Contacted Helper App */


public class ActivityHelp extends ServiceActivity implements View.OnClickListener {

    ImageView imgEmail;
    ImageView imgPhone;
    ImageView imgWeb;
    TextView titleTxt;

    String phone = "";
    String email = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.help));
        findviewById();
        setOnClickListener();
        getHelp();
    }

    // Defined Element
    private void findviewById() {
        imgEmail = (ImageView) findViewById(R.id.img_mail);
        imgPhone = (ImageView) findViewById(R.id.img_phone);
        imgWeb = (ImageView) findViewById(R.id.img_web);
        titleTxt = (TextView) findViewById(R.id.title_txt);
        titleTxt.setText(getString(R.string.app_name) +" "+ getString(R.string.help));
    }

    // Defined Clicked Listener
    private void setOnClickListener() {
        imgEmail.setOnClickListener(this);
        imgPhone.setOnClickListener(this);
        imgWeb.setOnClickListener(this);
    }

    // On Clicked Method
    @Override
    public void onClick(View v) {
        if (v == imgEmail) { // If Cliked Image Email
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/html");
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
            intent.putExtra(Intent.EXTRA_SUBJECT,getString(R.string.app_name)+"-"+getString(R.string.help));
            intent.putExtra(Intent.EXTRA_TEXT, "Hello team");
            startActivity(Intent.createChooser(intent, "Send Email"));
        }
        if (v == imgPhone) { // If Cliked Image Email
            if (phone != null && !phone.equalsIgnoreCase("null") &&
                    !phone.equalsIgnoreCase("") && phone.length() > 0) {

                Intent intentCall = new Intent(Intent.ACTION_DIAL);
                intentCall.setData(Uri.parse("tel:" + phone));
                startActivity(intentCall);

            }else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder .setTitle(getString(R.string.app_name))
                        .setIcon(R.mipmap.ic_launcher)
                        .setMessage(getString(R.string.sorry_for_inconvinent))
                        .setCancelable(false)
                        .setPositiveButton("ok",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                     dialog.dismiss();
                                    }
                                });
                AlertDialog alert1 = builder.create();
                alert1.show();
            }
        }
        if (v == imgWeb) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(URLHelper.REDIRECT_URL));
            startActivity(browserIntent);
        }
    }

    // Selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home)
            onBackPressed(); // Invoke Method Back Pressed
        return super.onOptionsItemSelected(item);
    }

    // Checked A Permissions Call & Send Email
    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + phone));
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    startActivity(intent);
                } else {

                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    } else {
                        boolean showRationale = shouldShowRequestPermissionRationale(Manifest.permission.CALL_PHONE);

                    }
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    // Method Get Phone & Email Support App
    public void getHelp() {
        final LoadingDialog loadingDialog = new LoadingDialog(this);
        loadingDialog.setCancelable(false);
        loadingDialog.show();
        JSONObject object = new JSONObject();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URLHelper.HELP, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                loadingDialog.dismiss();
                phone = response.optString("contact_number"); // Get Phone Support
                email = response.optString("contact_email"); // Get Email Support
        if(phone.equals(""))
            phone="+201096692052";
        if(email.equals(""))
            email="farid123fathy@gmail.com";


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
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
                                e.printStackTrace();
                            }
                        } else if (response.statusCode == 401) {
                            refreshAccessToken();
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
                        e.printStackTrace();
                    }

                } else {
                    if (error instanceof NoConnectionError) {
                        displayMessage(getString(R.string.oops_connect_your_internet));
                    } else if (error instanceof NetworkError) {
                        displayMessage(getString(R.string.oops_connect_your_internet));
                    } else if (error instanceof TimeoutError) {
                        getHelp();
                    }
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "Bearer " + SharedHelper.getKey(ActivityHelp.this, "access_token"));
                return headers;
            }
        };
        App.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    // Method Refresh Get Access Token (Auth Token Login) From Server
    private void refreshAccessToken() {

        JSONObject object = new JSONObject();
        try {

            object.put("grant_type", "refresh_token");
            object.put("client_id", URLHelper.client_id);
            object.put("client_secret", URLHelper.client_secret);
            object.put("refresh_token", SharedHelper.getKey(getApplicationContext(), "refresh_token"));
            object.put("scope", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.login, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                SharedHelper.putKey(ActivityHelp.this, "access_token", response.optString("access_token"));
                SharedHelper.putKey(ActivityHelp.this, "refresh_token", response.optString("refresh_token"));
                SharedHelper.putKey(ActivityHelp.this, "token_type", response.optString("token_type"));
                getHelp();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String json = null;
                String Message;
                NetworkResponse response = error.networkResponse;

                if (response != null && response.data != null) {
                    SharedHelper.putKey(ActivityHelp.this, "loggedIn", getString(R.string.False));
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

    // Method Popup Message Snackbar
    public void displayMessage(String toastString) {
        Snackbar.make(getCurrentFocus(), toastString, Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();
    }

    // Method Go To Begin Activity
    public void GoToBeginActivity() {
        SharedHelper.putKey(this, "loggedIn", getString(R.string.False));
        Intent mainIntent = new Intent(this, BeginScreen.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
        this.finish();
    }





    private boolean permissionAlreadyGranted() {

        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);

        if (result == PackageManager.PERMISSION_GRANTED)
            return true;

        return false;
    }

}
