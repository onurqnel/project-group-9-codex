package com.example.otams.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.otams.R;

public class LandingPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        Button btnGetStarted = findViewById(R.id.getStarted);
        btnGetStarted.setOnClickListener(view -> {
            startActivity(new Intent(this, LoginPage.class));
            finish();
        });
    }
}
