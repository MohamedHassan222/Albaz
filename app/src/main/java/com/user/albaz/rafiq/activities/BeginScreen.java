package com.user.albaz.rafiq.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;

import com.user.albaz.rafiq.R;
import com.phonenumberui.CountryCodeActivity;
import com.phonenumberui.countrycode.Country;
import com.phonenumberui.countrycode.CountryUtils;
import com.phonenumberui.utility.Utility;

public class BeginScreen extends AppCompatActivity {

    LinearLayout social_layout;

    private AppCompatEditText etCountryCode;
    private Button etPhoneNumber;
    private ImageView imgFlag;
    private Activity mActivity = BeginScreen.this;
    private Country mSelectedCountry;
    private static final int COUNTRYCODE_ACTION = 1;
    private static final int VERIFICATION_ACTION = 2;
    public String title = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_begin);

        // Invoke Set Initialize Elements And Set Defult Country
        setUpUI();

        // Clicked EditText Phone
        etPhoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainIntent = new Intent(BeginScreen.this, ActivityPhone.class);
                //   mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(mainIntent);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                // BeginScreen.this.finish();
            }
        });

        // Clicked Social TextView
        social_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(BeginScreen.this, ActivitySocialLogin.class);
                //   mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(mainIntent);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        });

    }

    // Initialize Element And Set Default Country
    private void setUpUI() {

        social_layout = findViewById(R.id.social_layout);
        etCountryCode = findViewById(com.phonenumberui.R.id.etCountryCode);
        etPhoneNumber = findViewById(com.phonenumberui.R.id.etPhoneNumber);
        imgFlag = findViewById(com.phonenumberui.R.id.flag_imv);

        TelephonyManager tm = (TelephonyManager) getSystemService(getApplicationContext().TELEPHONY_SERVICE);
        String countryISO = tm.getNetworkCountryIso();
        String countryNumber = "";
        String countryName = "";
        Utility.log(countryISO);

        if(!TextUtils.isEmpty(countryISO)) {

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

        // If Cliked In Select Country
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

    }

    // Result Activity Country
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

}
