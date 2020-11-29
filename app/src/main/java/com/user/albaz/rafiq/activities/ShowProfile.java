package com.user.albaz.rafiq.activities;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.user.albaz.rafiq.R;
import com.user.albaz.rafiq.helper.ConnectionHelper;
import com.user.albaz.rafiq.helper.LoadingDialog;
import com.user.albaz.rafiq.helper.URLHelper;
import com.user.albaz.rafiq.models.Driver;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

public class ShowProfile extends AppCompatActivity {

    public Context context = ShowProfile.this;
    public Activity activity = ShowProfile.this;
    String TAG = "ShowActivity";
    LoadingDialog loadingDialog;
    ConnectionHelper helper;
    Boolean isInternet;
    ImageView backArrow;
    TextView email, first_name, last_name, mobile_no, services_provided;
    ImageView profile_Image;
    RatingBar ratingProvider;
    String strUserId = "", strServiceRequested = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check SDK Environment & Set Attributes
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }
        setContentView(R.layout.activity_show_profile);

        findViewByIdandInitialization(); // Invoke Initialize Elements

        // Clicked BackArrow
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    // Initialization Elements & Set Values By Instance Driver Class
    public void findViewByIdandInitialization() {
        email =  findViewById(R.id.email);
        first_name =  findViewById(R.id.first_name);
        last_name =  findViewById(R.id.last_name);
        mobile_no =  findViewById(R.id.mobile_no);
        //services_provided = findViewById(R.id.services_provided);
        backArrow =  findViewById(R.id.backArrow);
        profile_Image = findViewById(R.id.img_profile);
        ratingProvider =  findViewById(R.id.ratingProvider);
        helper = new ConnectionHelper(context);
        isInternet = helper.isConnectingToInternet();

        Driver provider = getIntent().getParcelableExtra("driver"); // Instance Driver
        if (provider != null) {
            if (provider.getEmail() != null && !provider.getEmail().equalsIgnoreCase("null") && provider.getEmail().length() > 0)
                email.setText(provider.getEmail());
            else
                email.setText("");
            if (provider.getFname() != null && !provider.getFname().equalsIgnoreCase("null") && provider.getFname().length() > 0)
                first_name.setText(provider.getFname());
            else
                first_name.setText("");
            if (provider.getMobile() != null && !provider.getMobile().equalsIgnoreCase("null") && provider.getMobile().length() > 0)
                mobile_no.setText(provider.getMobile());
            else
                mobile_no.setText(getString(R.string.user_no_mobile));
            if (provider.getLname() != null && !provider.getLname().equalsIgnoreCase("null") && provider.getLname().length() > 0)
                last_name.setText(provider.getLname());
            else
                last_name.setText("");
            if (provider.getRating() != null && !provider.getRating().equalsIgnoreCase("null") && provider.getRating().length() > 0)
                ratingProvider.setRating(Float.parseFloat(provider.getRating()));
            else
                ratingProvider.setRating(1);
            if (provider.getImg().startsWith("http"))
                Picasso.with(context).load(provider.getImg()).memoryPolicy(MemoryPolicy.NO_CACHE).placeholder(R.drawable.ic_dummy_user).error(R.drawable.ic_dummy_user).into(profile_Image);
            else
                Picasso.with(context).load(URLHelper.base_pic +provider.getImg()).memoryPolicy(MemoryPolicy.NO_CACHE).placeholder(R.drawable.ic_dummy_user).error(R.drawable.ic_dummy_user).into(profile_Image);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    // Display Toast Message
    public void displayMessage(String toastString) {
        //Toast.makeText(context, toastString + "", Toast.LENGTH_SHORT).show();
    }


}
