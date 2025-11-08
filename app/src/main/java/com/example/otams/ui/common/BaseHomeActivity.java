package com.example.otams.ui.common;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.LayoutRes;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import com.example.otams.ui.auth.LoginPage;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Shared behaviour for simple home screens that only expose a logout button.
 */
public abstract class BaseHomeActivity extends AppCompatActivity {

    @LayoutRes
    protected abstract int getLayoutResId();

    protected abstract int getLogoutButtonId();

    @StringRes
    protected int getLogoutMessageResId() {
        return android.R.string.ok;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResId());

        Button logoutButton = findViewById(getLogoutButtonId());
        logoutButton.setOnClickListener(v -> logout());
    }

    protected void logout() {
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(this, getLogoutMessageResId(), Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, LoginPage.class));
        finish();
    }
}
