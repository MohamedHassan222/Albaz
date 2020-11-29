package com.phonenumberui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.phonenumberui.countrycode.Country;
import com.phonenumberui.countrycode.CountryUtils;
import com.phonenumberui.utility.Utility;

import io.michaelrocks.libphonenumber.android.NumberParseException;
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil;
import io.michaelrocks.libphonenumber.android.Phonenumber;

public class PhoneNumberActivity extends AppCompatActivity {

    private AppCompatEditText etCountryCode;
    private AppCompatEditText etPhoneNumber;
    private AppCompatButton btnSendConfirmationCode;
    private ImageView imgFlag;
    private AppCompatTextView tvToolbarTitle;
    private Activity mActivity = PhoneNumberActivity.this;
    private PhoneNumberUtil mPhoneUtil;
    private Country mSelectedCountry;
    private static final int COUNTRYCODE_ACTION = 1;
    private static final int VERIFICATION_ACTION = 2;
    public String title = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_number);
        setUpUI();
        setUpToolBar();
    }


    // Method Has Defined Element & SetUp UI
    private void setUpUI() {
        etCountryCode = findViewById(R.id.etCountryCode);
        imgFlag = findViewById(R.id.flag_imv);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        btnSendConfirmationCode = findViewById(R.id.btnSendConfirmationCode);
        tvToolbarTitle = findViewById(R.id.tvToolbarTitle);
        mPhoneUtil = PhoneNumberUtil.createInstance(mActivity);
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
            Country country = new Country(countryISO,
                    countryNumber,
                    countryName);
            this.mSelectedCountry = country;
            etCountryCode.setText("+" + country.getPhoneCode() + "");
            imgFlag.setImageResource(CountryUtils.getFlagDrawableResId(country.getIso()));
            Utility.log(countryNumber);

        } else {
            Country country = new Country(getString(R.string.country_united_states_code),
                    getString(R.string.country_united_states_number),
                    getString(R.string.country_united_states_name));
            this.mSelectedCountry = country;
            etCountryCode.setText("+" + country.getPhoneCode() + "");
            imgFlag.setImageResource(CountryUtils.getFlagDrawableResId(country.getIso()));
            Utility.log(countryNumber);
        }

        setPhoneNumberHint();

        // Cliked On In Chose Country Btn
        etCountryCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Utility.hideKeyBoardFromViewFromView(mActivity);
                etPhoneNumber.setError(null);
                Intent intent = new Intent(mActivity, CountryCodeActivity.class);
                intent.putExtra("TITLE", getResources().getString(R.string.app_name));
                startActivityForResult(intent, COUNTRYCODE_ACTION);
            }
        });

        // Cliked On In Confirmation Code
        btnSendConfirmationCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.hideKeyBoardFromView(mActivity);
                etPhoneNumber.setError(null);
                Log.v("aaaaaaa","btnSendConfirmationCode before validate");
                if (validate()) {
                    Log.v("aaaaaaa","btnSendConfirmationCode after validate");
                    Intent verificationIntent = new Intent(mActivity, VerificationCodeActivity.class);
                    verificationIntent.putExtra(AppConstant.PhoneNumber, etPhoneNumber.getText().toString().trim());
                    verificationIntent.putExtra(AppConstant.PhoneCode, mSelectedCountry.getPhoneCode() + "");
                    verificationIntent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
                    startActivity(verificationIntent);
                    finish();
                }
            }
        });

        // Set Phone Number New Activity In EditText
        if (getIntent().getExtras() != null) {
            if (getIntent().hasExtra("PHONE_NUMBER")) {
                etPhoneNumber.setText(getIntent().getStringExtra("PHONE_NUMBER"));
                etPhoneNumber.setSelection(etPhoneNumber.getText().toString().trim().length());
            }
        }
    }

    // Execute ToolBar
    private void setUpToolBar() {
        Toolbar mToolBar = findViewById(R.id.toolbar);
        mToolBar.setTitleTextColor(ContextCompat.getColor(mActivity, R.color.white));
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        if (getIntent().hasExtra("TITLE") && getIntent().getStringExtra("TITLE") != null && !getIntent().getStringExtra("TITLE").equalsIgnoreCase("")) {
            title = getIntent().getStringExtra("TITLE");
            getSupportActionBar().setTitle(title);

        } else

        {
            getSupportActionBar().setTitle("Enter your Phone Number");

        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    // Set Hint EditText Phone
    private void setPhoneNumberHint() {
        if (mSelectedCountry != null) {
            Phonenumber.PhoneNumber phoneNumber = mPhoneUtil.getExampleNumberForType(mSelectedCountry.getIso().toUpperCase(),
                    PhoneNumberUtil.PhoneNumberType.MOBILE);
            if (phoneNumber != null) {
                String format = mPhoneUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.E164);
                if (format.length() > mSelectedCountry.getPhoneCode().length())
                    etPhoneNumber.setHint(
                            format.substring((mSelectedCountry.getPhoneCode().length() + 1), format.length()));
            }
        }
    }

    // Vailedate Phone Failed
    private boolean validate() {
        if (TextUtils.isEmpty(etPhoneNumber.getText().toString().trim())) {
            etPhoneNumber.setError("Please enter phone number");
            etPhoneNumber.requestFocus();
            return false;
        } else if (!isValid()) {
            etPhoneNumber.setError("Please enter valid phone number");
            etPhoneNumber.requestFocus();
            return false;
        }
        return true;
    }

    // Result Country Name by Activity Country
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == COUNTRYCODE_ACTION) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    if (data.hasExtra(AppConstant.COUNTRY)) {
                        Country country = (Country) data.getSerializableExtra(AppConstant.COUNTRY);
                        this.mSelectedCountry = country;
                        setPhoneNumberHint();
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

    // Validate Number
    public boolean isValid() {
        Phonenumber.PhoneNumber phoneNumber = getPhoneNumber();
        return phoneNumber != null && mPhoneUtil.isValidNumber(phoneNumber);
    }

    // Check Number
    public Phonenumber.PhoneNumber getPhoneNumber() {
        try {
            String iso = null;
            if (mSelectedCountry != null) {
                iso = mSelectedCountry.getIso().toUpperCase();
            }
            return mPhoneUtil.parse(etPhoneNumber.getText().toString().trim(), iso);
        } catch (NumberParseException ignored) {
            ignored.printStackTrace();
            return null;
        }
    }
}
