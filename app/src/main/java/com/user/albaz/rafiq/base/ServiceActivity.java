package com.user.albaz.rafiq.base;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.user.albaz.rafiq.R;
import com.user.albaz.rafiq.helper.SharedHelper;
import com.phonenumberui.PhoneNumberActivity;

import java.util.HashMap;

public class ServiceActivity extends BaseActivity {

    public static int APP_REQUEST_CODE = 99;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    // Result Activity
    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }

        if (requestCode == APP_REQUEST_CODE) { // Check Request Code
            String phone = data.getStringExtra("PHONE_NUMBER");
            if (true) {
            if (phone != null && !phone.equals("")) {

            }
                String uid = data.getStringExtra("UID");
                String token = data.getStringExtra("TOKEN");

                if (token != null && !token.equals("")) {
                    SharedHelper.putKey(this, "account_kit_token", token);
                    SharedHelper.putKey(this, "mobile", phone);
                    //SharedHelper.putKey(context, "account_kit", getString(R.string.True));
                    onPhoneVerified(uid, phone, token);
                } else {
                    SharedHelper.putKey(this, "account_kit_token", "");
                    SharedHelper.putKey(this, "loggedIn", getString(R.string.False));
                    SharedHelper.putKey(this, "email", "");
                    SharedHelper.putKey(this, "login_by", "");
                    //SharedHelper.putKey(context, "account_kit", getString(R.string.False));
                    goToBeginActivity();
                    finish();
                }

            } else {
                // If mobile number is not verified successfully You can hendle according to your requirement.
                String msg = data.getStringExtra("MSG");
                if (msg != null) {
                    Log("onError: Account Kit" + msg);
                    //SharedHelper.putKey(context, "account_kit", getString(R.string.False));
                    //Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
                }

            }
        }
    }

    public final HashMap<String, String> createHeader() {
        return createHeader(null);
    }

    // Method Sending Toking To Server
    public final HashMap<String, String> createHeader(String token) {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("X-Requested-With", "XMLHttpRequest");
        if (token != null && !token.trim().equals("")) {
            headers.put("Authorization", token);
        }
        return headers;
    }

    // The Method Go To PhoneActivityVerify
    public final void verifyPhone(String phone) {
        Log.v("verifyPhone","verifyPhone "+phone);
        Intent intent = new Intent(this, PhoneNumberActivity.class);
        //Optionally you can add toolbar title
        intent.putExtra("TITLE", getResources().getString(R.string.app_name));
        //Optionally you can pass phone number to populate automatically.
        intent.putExtra("PHONE_NUMBER", phone);
        startActivityForResult(intent, APP_REQUEST_CODE);
    }

    // Return Token Device
    public final String getDeviceToken() {
        String s = "";
        try {
            s = SharedHelper.getKey(getApplicationContext(), "device_token");
            if (s != null && !s.equalsIgnoreCase("")) {
                Log("GCM Registration Token: " + s);
            } else {
                s = "COULD NOT GET FCM TOKEN";
                Log("Failed to complete token refresh: " + s);
            }
        } catch (Exception e) {
            s = "COULD NOT GET FCM TOKEN";
            Log("Failed to complete token refresh");
        }
        return s;
    }

    // Return ID Device
    public final String getDeviceUDID() {
        String s = "";
        try {
            s = android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
            Log("Device UDID:" + s);
        } catch (Exception e) {
            s = "COULD NOT GET UDID";
            e.printStackTrace();
            Log("Failed to complete device UDID");
        }
        return s;
    }

    public void onPhoneVerified(String uid, String phone, String token) {

    }

}
