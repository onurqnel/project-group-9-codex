package com.example.otams.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.otams.R;
import com.example.otams.model.UserRole;
import com.example.otams.ui.registration.StudentRegistrationPage;
import com.example.otams.ui.registration.TutorRegistrationPage;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterPage extends AppCompatActivity {

    private TextInputEditText editTextEmail;
    private TextInputEditText editTextPassword;
    private Button buttonRegister;
    private ProgressBar progressBar;
    private RadioGroup radioGroupRole;
    private RadioButton radioStudent;
    private RadioButton radioTutor;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        auth = FirebaseAuth.getInstance();

        progressBar = findViewById(R.id.progressBar);
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        buttonRegister = findViewById(R.id.btn_register);
        radioGroupRole = findViewById(R.id.radioGroupRole);
        radioStudent = findViewById(R.id.radioStudent);
        radioTutor = findViewById(R.id.radioTutor);

        buttonRegister.setOnClickListener(view -> attemptRegistration());
    }

    private void attemptRegistration() {
        progressBar.setVisibility(View.VISIBLE);
        buttonRegister.setEnabled(false);

        String email = text(editTextEmail);
        String password = text(editTextPassword);

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || radioGroupRole.getCheckedRadioButtonId() == -1) {
            toast(R.string.registration_fill_all_fields);
            resetProgress();
            return;
        }

        UserRole role = radioStudent.isChecked() ? UserRole.STUDENT : UserRole.TUTOR;

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        toastLong(getString(R.string.registration_failed, task.getException() != null ? task.getException().getMessage() : ""));
                        resetProgress();
                        return;
                    }

                    FirebaseUser user = auth.getCurrentUser();
                    if (user == null) {
                        toastLong(getString(R.string.registration_user_null));
                        resetProgress();
                        return;
                    }

                    toast(R.string.registration_account_created);
                    resetProgress();
                    navigateToProfile(role);
                });
    }

    private void navigateToProfile(UserRole role) {
        Intent intent = role == UserRole.STUDENT
                ? new Intent(this, StudentRegistrationPage.class)
                : new Intent(this, TutorRegistrationPage.class);
        startActivity(intent);
        finish();
    }

    private void resetProgress() {
        progressBar.setVisibility(View.GONE);
        buttonRegister.setEnabled(true);
    }

    private void toast(int messageRes) {
        Toast.makeText(this, messageRes, Toast.LENGTH_SHORT).show();
    }

    private void toastLong(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private String text(TextInputEditText input) {
        return input.getText() == null ? "" : input.getText().toString().trim();
    }
}
