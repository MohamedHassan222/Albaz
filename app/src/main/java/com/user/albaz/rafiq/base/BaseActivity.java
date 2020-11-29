package com.user.albaz.rafiq.base;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;

import android.util.Base64;
import android.view.Gravity;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.user.albaz.rafiq.R;
import com.user.albaz.rafiq.activities.BeginScreen;
import com.user.albaz.rafiq.activities.MainActivity;
import com.user.albaz.rafiq.helper.LoadingDialog;
import com.user.albaz.rafiq.helper.SharedHelper;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static com.user.albaz.rafiq.App.trimMessage;

public class BaseActivity extends AppCompatActivity {

    private LoadingDialog _loadingDialog;
    Dialog dialog;
    private boolean showLog = true;
    private String _TAG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        dialog = new LoadingDialog(BaseActivity.this);
        dialog.setContentView(R.layout.dialog_loading);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.CENTER);

    }

    // Method Printed Key Hash In Package App
    public static final String printKeyHash(Activity context) {
        PackageInfo packageInfo;
        String key = null;
        try {
            //getting application package name, as defined in manifest
            String packageName = context.getApplicationContext().getPackageName();

            // Retrieving package info
            packageInfo = context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_SIGNATURES);

            for (Signature signature : packageInfo.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                key = new String(Base64.encode(md.digest(), 0));

                // String key = new String(Base64.encodeBytes(md.digest()));
            }
        } catch (PackageManager.NameNotFoundException e1) {
        } catch (NoSuchAlgorithmException e) {
        } catch (Exception e) {
        }

        return key;
    }

    // Set Tag Log
    public final void setLogTag(String tag) {
        _TAG = tag;
    }

    // Show Log
    public final void Log(String s) {
        if (showLog) {
        }
    }

    // Method On Error Volley Internet
    public final void handleErrorResponse(VolleyError error) {
        String json = null;
        String Message;
        NetworkResponse response = error.networkResponse;
        if (response != null && response.data != null) {
            try {
                JSONObject errorObj = new JSONObject(new String(response.data));

                if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                    try {
                        displaySnackbar(errorObj.getString("message"));
                    } catch (Exception e) {
                        displaySnackbar(getString(R.string.something_went_wrong));
                    }
                } else if (response.statusCode == 401) {
                    /*refreshAccessToken();*/
                } else if (response.statusCode == 422) {

                    json = trimMessage(new String(response.data));
                    if (json != "" && json != null) {
                        displaySnackbar(json);
                    } else {
                        displaySnackbar(getString(R.string.please_try_again));
                    }

                } else if (response.statusCode == 503) {
                    displaySnackbar(getString(R.string.server_down));
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
            }
        }
    }

    // Show Dialog Loading
    public final void showLoading() {
        dialog.show();
//        _loadingDialog = new LoadingDialog(BaseActivity.this);
//        _loadingDialog.setCancelable(false);
//        if (_loadingDialog != null)
//            _loadingDialog.show();
    }

    // Destroy Dialog Loading
    public final void dismissLoading() {
        dialog.dismiss();
//        if (_loadingDialog != null && _loadingDialog.isShowing()) {
//            _loadingDialog.dismiss();
//            _loadingDialog = null;
//    }

    }

    // Checking The Internet
    public boolean hasInternet() {
        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivity.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            //Toast.makeText(context, "Oops ! Connect your Internet", Toast.LENGTH_LONG).show();
            return false;
        }

    }

    // Check Boolean Value Internet
    public final boolean requestInternet() {
        if (hasInternet()) {
            return true;
        }
        //mProgressDialog.dismiss();
        AlertDialog.Builder builder = new AlertDialog.Builder(BaseActivity.this);
        builder.setMessage("Check your Internet").setCancelable(false);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("Setting", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Intent NetworkAction = new Intent(Settings.ACTION_SETTINGS);
                startActivity(NetworkAction);

            }
        });
        builder.show();
        return false;
    }

    // Display SnakBar
    public final void displaySnackbar(String toastString) {
        View v = getCurrentFocus();
        if (v != null) {
            Snackbar.make(this.getCurrentFocus(), toastString, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }

    // Method Go To Main Activity
    public final void goToMainActivity() {
        Intent mainIntent = new Intent(BaseActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
        this.finish();
    }

    // Method Go To Begin Activity
    public final void goToBeginActivity() {
        SharedHelper.putKey(getApplicationContext(), "loggedIn", getString(R.string.False));
        Intent mainIntent = new Intent(this, BeginScreen.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
        finish();
    }


}
