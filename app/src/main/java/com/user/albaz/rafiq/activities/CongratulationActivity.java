package com.user.albaz.rafiq.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.user.albaz.rafiq.R;

public class CongratulationActivity extends AppCompatActivity {

    ConstraintLayout cancelLayout, congratulation;
    TextView msg, txtView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_congratulation);
        cancelLayout = findViewById(R.id.cancel_trip);
        congratulation = findViewById(R.id.congratulation_lotie);
        msg = findViewById(R.id.msg);
        txtView = findViewById(R.id.textView5);

        Intent i = getIntent();

        if (i != null) {
            String s = i.getStringExtra("trip");
            if (s.equals("cancel")) {
                cancelLayout.setVisibility(View.VISIBLE);
                congratulation.setVisibility(View.GONE);
                txtView.setText(getString(R.string.trip_is_canceled_successfully));
            }
            if (s.equals("schedule")) {
                String status = i.getStringExtra("status");


                if (status.equals("1")) {
                    cancelLayout.setVisibility(View.GONE);
                    congratulation.setVisibility(View.VISIBLE);
                    msg.setText((R.string.schedule_success));
                } else {
                    cancelLayout.setVisibility(View.VISIBLE);
                    congratulation.setVisibility(View.GONE);
                    msg.setText((R.string.canot_schedule));

                }
            } else {
                cancelLayout.setVisibility(View.GONE);
                congratulation.setVisibility(View.VISIBLE);
            }
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    this.finalize();

                    Intent intent = (new Intent(CongratulationActivity.this, MainActivity.class));
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    //intent.putExtra("EXTRA_CANCELLED_ALREADY",true);
                    startActivity(intent);
                    //Toast.makeText(CongratulationActivity.this, "Executed", Toast.LENGTH_SHORT).show();

                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }

            }
        }, 2000);
    }

}
