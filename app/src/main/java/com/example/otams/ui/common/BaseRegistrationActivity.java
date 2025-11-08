package com.example.otams.ui.common;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.otams.model.RegistrationRequest;
import com.example.otams.model.UserRole;
import com.example.otams.ui.auth.LoginPage;
import com.example.otams.ui.registration.PendingPage;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/**
 * Base activity for tutor/student registration flows.
 */
public abstract class BaseRegistrationActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private FirebaseUser currentUser;

    @LayoutRes
    protected abstract int getLayoutResId();

    @IdRes
    protected abstract int getProgressBarId();

    @IdRes
    protected abstract int getSignUpButtonId();

    @IdRes
    protected abstract int getLogoutButtonId();

    protected abstract void onFormCreated(FirebaseUser user);

    @Nullable
    protected abstract RegistrationRequest buildRequest(FirebaseUser user);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResId());

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        currentUser = auth.getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(this, getMissingUserMessage(), Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginPage.class));
            finish();
            return;
        }

        progressBar = findViewById(getProgressBarId());
        findViewById(getSignUpButtonId()).setOnClickListener(v -> submit());
        findViewById(getLogoutButtonId()).setOnClickListener(v -> logout());

        onFormCreated(currentUser);
    }

    protected String getMissingUserMessage() {
        return "No user signed in. Login.";
    }

    protected FirebaseFirestore getFirestore() {
        return firestore;
    }

    protected FirebaseUser getCurrentUser() {
        return currentUser;
    }

    protected void submit() {
        RegistrationRequest request = buildRequest(currentUser);
        if (request == null) {
            return;
        }
        showProgress(true);
        firestore.collection("requests")
                .add(request)
                .addOnSuccessListener(docRef -> docRef.update("requestId", docRef.getId())
                        .addOnSuccessListener(aVoid -> {
                            sendConfirmation(request.getEmail(), request.getRoleEnum());
                            showProgress(false);
                            onSubmissionSuccess();
                        })
                        .addOnFailureListener(this::handleSubmissionError))
                .addOnFailureListener(this::handleSubmissionError);
    }

    protected void onSubmissionSuccess() {
        startActivity(new Intent(this, PendingPage.class));
        finish();
    }

    private void handleSubmissionError(Exception exception) {
        showProgress(false);
        Toast.makeText(this, "Failed to submit: " + exception.getMessage(), Toast.LENGTH_LONG).show();
    }

    protected void logout() {
        auth.signOut();
        Toast.makeText(this, getLogoutMessage(), Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, LoginPage.class));
        finish();
    }

    protected String getLogoutMessage() {
        return "Log out successful.";
    }

    protected void showProgress(boolean show) {
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    protected void sendConfirmation(String email, @Nullable UserRole role) {
        if (email == null) {
            return;
        }
        Map<String, Object> mail = new HashMap<>();
        mail.put("to", email);

        Map<String, Object> template = new HashMap<>();
        template.put("role", role != null ? role.getFirestoreValue() : "");
        template.put("displayEmail", email);
        mail.put("templateData", template);
        mail.put("createdAt", Timestamp.now());

        firestore.collection("mail")
                .add(mail);
    }

    protected String text(TextInputEditText input) {
        return input.getText() == null ? "" : input.getText().toString().trim();
    }
}
