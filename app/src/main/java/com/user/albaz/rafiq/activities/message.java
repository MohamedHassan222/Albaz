package com.user.albaz.rafiq.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.user.albaz.rafiq.R;

public class message extends AppCompatActivity {

    private String message;

    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        Intent iin = getIntent();
        Bundle b = iin.getExtras();
        message = b.get("message").toString(); // Set Message

        textView = findViewById(R.id.message);
        textView.setText(message); // Set Message In TextView
    }
}
