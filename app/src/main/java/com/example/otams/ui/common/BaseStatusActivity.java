package com.example.otams.ui.common;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.otams.R;
import com.example.otams.ui.auth.LoginPage;

/**
 * Base activity for displaying simple status messages with a back button.
 */
public abstract class BaseStatusActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResId());

        TextView title = findViewById(R.id.statusTitleText);
        title.setText(getTitleTextResId());
        title.setTextColor(ContextCompat.getColor(this, getTitleColorResId()));

        TextView description = findViewById(R.id.statusDescriptionText);
        int descriptionRes = getDescriptionTextResId();
        if (descriptionRes != 0) {
            description.setText(descriptionRes);
            description.setVisibility(View.VISIBLE);
        } else {
            description.setVisibility(View.GONE);
        }

        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> navigateBack());
    }

    @LayoutRes
    protected int getLayoutResId() {
        return R.layout.activity_status_screen;
    }

    @StringRes
    protected abstract int getTitleTextResId();

    @ColorRes
    protected abstract int getTitleColorResId();

    @StringRes
    protected int getDescriptionTextResId() {
        return 0;
    }

    protected void navigateBack() {
        startActivity(new Intent(this, LoginPage.class));
        finish();
    }
}
