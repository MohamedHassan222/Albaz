package com.user.albaz.rafiq.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;

import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatEditText;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.user.albaz.rafiq.App;
import com.user.albaz.rafiq.R;
import com.user.albaz.rafiq.base.ServiceActivity;
import com.user.albaz.rafiq.helper.LoadingDialog;
import com.user.albaz.rafiq.helper.SharedHelper;
import com.user.albaz.rafiq.helper.URLHelper;
import com.user.albaz.rafiq.utils.MyTextView;
import com.user.albaz.rafiq.utils.Utilities;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.phonenumberui.CountryCodeActivity;
import com.phonenumberui.countrycode.Country;
import com.phonenumberui.countrycode.CountryUtils;
import com.phonenumberui.utility.Utility;

import java.util.HashMap;
import java.util.Map;


public class ActivityPhone extends ServiceActivity {

    ImageView backArrow;
    FloatingActionButton nextICON;
    EditText email;
    MyTextView register, forgetPassword;
    Dialog dialog;
    String phone;
    TextView codeTV;
    private AppCompatEditText etCountryCode;
    private ImageView imgFlag;
    private AppCompatEditText etPhoneNumber;

    private Activity mActivity = ActivityPhone.this;
    private Country mSelectedCountry;
    private static final int COUNTRYCODE_ACTION = 1;
    private static final int VERIFICATION_ACTION = 2;
    public String title = "";

    private String IDPhone; // Device ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);

        if (Build.VERSION.SDK_INT > 15) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }


        init();
        action();

        setUpUI(); // Invoke Method SetUp Elements

    }


    private void init() {

        // Info Device
        IDPhone = getDeviceUDID();

        // Initialize Elements
        nextICON = findViewById(R.id.right_arrow);
        backArrow = findViewById(R.id.backArrow);
        register = findViewById(R.id.register);
        forgetPassword = findViewById(R.id.forgetPassword);
        etCountryCode = findViewById(com.phonenumberui.R.id.etCountryCode);
        etPhoneNumber = findViewById(com.phonenumberui.R.id.etPhoneNumber);

        dialog = new LoadingDialog(ActivityPhone.this);
        dialog.setContentView(R.layout.dialog_loading);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.CENTER);


    }

    private void action() {


        // Clicked NextIcon
        etPhoneNumber.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (Utilities.checkPhone(getApplicationContext(), etPhoneNumber.getText().toString())) {
                        check(URLHelper.CHECK); // Invoke Method Check
                    }
                    return true;
                } else
                    return false;
            }
        });
        nextICON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utilities.checkPhone(getApplicationContext(),
                        etPhoneNumber.getText().toString())) {
                    check(URLHelper.CHECK); // Invoke Method Check
                }
            }

        });

        // Clicked BackArrow
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        // Clicked Register
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedHelper.putKey(ActivityPhone.this, "password", "");
                Intent mainIntent = new Intent(ActivityPhone.this, RegisterActivity.class);
                mainIntent.putExtra("isFromMailActivity", true);
                startActivity(mainIntent);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        });

        // Clicked ForgetPassword
        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedHelper.putKey(ActivityPhone.this, "password", "");
                Intent mainIntent = new Intent(ActivityPhone.this, ForgetPassword.class);
                mainIntent.putExtra("isFromMailActivity", true);
                startActivity(mainIntent);
            }
        });
    }

    // Display SnackBar Message
    public void displayMessage(String toastString) {
        try {
            Snackbar.make(getCurrentFocus(), toastString, Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
        } catch (Exception e) {
            e.printStackTrace();
            //Toast.makeText(this, toastString, Toast.LENGTH_SHORT).show();
        }
    }

    // Clicked Back Pressed
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
    }

    // SetUp Elements
    private void setUpUI() {


        etPhoneNumber.setImeOptions(EditorInfo.IME_ACTION_DONE);

        imgFlag = findViewById(com.phonenumberui.R.id.flag_imv);
        codeTV = findViewById(com.phonenumberui.R.id.etCountryCode);

        TelephonyManager tm = (TelephonyManager) getSystemService(getApplicationContext().TELEPHONY_SERVICE);
        String countryISO = tm.getNetworkCountryIso();
        String countryNumber = "";
        String countryName = "";
        Utility.log(countryISO);


        if (!TextUtils.isEmpty(countryISO)) {

            for (Country country : CountryUtils.getAllCountries(mActivity)) {
                if (countryISO.toLowerCase().equalsIgnoreCase(country.getIso().toLowerCase())) {
                    countryNumber = country.getPhoneCode();
                    countryName = country.getName();
                    break;
                }
            }

            Country country = new Country(countryISO, countryNumber, countryName);
            this.mSelectedCountry = country;
            etCountryCode.setText("+" + country.getPhoneCode() + "");
            imgFlag.setImageResource(CountryUtils.getFlagDrawableResId(country.getIso()));
            Utility.log(countryNumber);

        } else {

            Country country = new Country(getString(com.phonenumberui.R.string.country_united_states_code),
                    getString(com.phonenumberui.R.string.country_united_states_number),
                    getString(com.phonenumberui.R.string.country_united_states_name));
            this.mSelectedCountry = country;
            etCountryCode.setText("+" + country.getPhoneCode() + "");
            imgFlag.setImageResource(CountryUtils.getFlagDrawableResId(country.getIso()));
            Utility.log(countryNumber);

        }


        etCountryCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.hideKeyBoardFromView(mActivity);
                etPhoneNumber.setError(null);
                Intent intent = new Intent(mActivity, CountryCodeActivity.class);
                intent.putExtra("TITLE", getResources().getString(com.phonenumberui.R.string.app_name));
                startActivityForResult(intent, COUNTRYCODE_ACTION);
            }
        });

        if (getIntent().getExtras() != null) {
            if (getIntent().hasExtra("PHONE_NUMBER")) {

                etPhoneNumber.setText(getIntent().getStringExtra("PHONE_NUMBER"));
                etPhoneNumber.setSelection(etPhoneNumber.getText().toString().trim().length());
            }
        }
    }

    // Result By Activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == COUNTRYCODE_ACTION) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    if (data.hasExtra("COUNTRY")) {
                        Country country = (Country) data.getSerializableExtra("COUNTRY");
                        this.mSelectedCountry = country;
                        etPhoneNumber.setHint("Enter Phone Number");
                        etCountryCode.setText("+" + country.getPhoneCode() + "");
                        imgFlag.setImageResource(CountryUtils.getFlagDrawableResId(country.getIso()));
                    }
                }
            }
        } else if (requestCode == VERIFICATION_ACTION) {
            if (data != null) {

            }
        }
    }

    // Check Phone Number Exist in DB Or Not
    public void check(final String URL) {

        phone = getCodeMobile() + etPhoneNumber.getText().toString();
        final String putMobile = etPhoneNumber.getText().toString();
        String newPhoneAfterReplace = "";

        if (phone.length() >= 14) { // Check Number Phone > 11 Number Or Not
            String Str = phone;
            newPhoneAfterReplace = Str.replaceFirst("0", "");
            phone = newPhoneAfterReplace;
        }
        dialog.show();
        // Prepare The Request
        StringRequest postRequest = new StringRequest(Request.Method.GET, URL + "?mobile=" + phone, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // response
                dialog.dismiss();
                if (response.equals("true")) {
                    SharedHelper.putKey(getApplicationContext(), "mobile", phone);
                    SharedHelper.putKey(getApplicationContext(), "putMobile", putMobile);
                    Intent intent = new Intent(getApplicationContext(), ActivityPassword.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

                } else {

                    SharedHelper.putKey(getApplicationContext(), "mobile", phone);
                    SharedHelper.putKey(getApplicationContext(), "putMobile", putMobile);
                    Toast.makeText(ActivityPhone.this, (R.string.wrong_phone), Toast.LENGTH_SHORT).show();


                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        dialog.dismiss();
                        // error
                        //Toast.makeText(mActivity, error.toString(), Toast.LENGTH_SHORT).show();
                    }

                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("mobile", phone);
                params.put("id_phone", IDPhone);
                params.put("Login_by", "manual");
                return params;
            }
        };

        // add it to the RequestQueue
        App.getInstance().addToRequestQueue(postRequest);
    }

    private String getCodeMobile() {
        String s = codeTV.getText().toString();
        if (s.equals("+20"))
            s = "+2";

        return s;
    }

}