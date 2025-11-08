package com.example.otams.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.otams.R;
import com.example.otams.model.RegistrationRequest;
import com.example.otams.model.RequestStatus;
import com.example.otams.model.UserRole;
import com.example.otams.ui.admin.AdministratorPage;
import com.example.otams.ui.home.StudentHome;
import com.example.otams.ui.home.TutorHome;
import com.example.otams.ui.registration.PendingPage;
import com.example.otams.ui.registration.RejectedPage;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginPage extends AppCompatActivity {

    private TextInputEditText editTextEmail;
    private TextInputEditText editTextPassword;
    private Button buttonLogin;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        progressBar = findViewById(R.id.progressBar);
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        buttonLogin = findViewById(R.id.btn_login);
        TextView textViewRegister = findViewById(R.id.registerNow);

        textViewRegister.setOnClickListener(view -> {
            startActivity(new Intent(this, RegisterPage.class));
            finish();
        });

        buttonLogin.setOnClickListener(view -> attemptLogin());
    }

    private void attemptLogin() {
        progressBar.setVisibility(View.VISIBLE);
        String email = text(editTextEmail);
        String password = text(editTextPassword);

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, R.string.login_enter_credentials, Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            return;
        }

        if (isAdministratorCredential(email, password)) {
            Toast.makeText(this, R.string.login_admin_success, Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, AdministratorPage.class));
            finish();
            return;
        }

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    if (!task.isSuccessful()) {
                        Toast.makeText(this,
                                getString(R.string.login_failed, task.getException() != null ? task.getException().getMessage() : ""),
                                Toast.LENGTH_LONG).show();
                        return;
                    }
                    String userId = auth.getCurrentUser().getUid();
                    checkUserStatusAndRedirect(userId);
                });
    }

    private boolean isAdministratorCredential(String email, String password) {
        return "admin@example.com".equals(email) && "admin123".equals(password);
    }

    private void checkUserStatusAndRedirect(String userId) {
        firestore.collection("requests")
                .whereEqualTo("userId", userId)
                .limit(1)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (querySnapshot.isEmpty()) {
                        Toast.makeText(this, R.string.login_no_request_found, Toast.LENGTH_LONG).show();
                        return;
                    }

                    DocumentSnapshot doc = querySnapshot.getDocuments().get(0);
                    RegistrationRequest request = doc.toObject(RegistrationRequest.class);
                    if (request == null) {
                        Toast.makeText(this, R.string.login_request_parse_error, Toast.LENGTH_LONG).show();
                        return;
                    }

                    routeUser(request);
                })
                .addOnFailureListener(e -> {
                    Log.e("LoginPage", "Firestore query failed for user: " + userId, e);
                    Toast.makeText(this, getString(R.string.login_status_error, e.getMessage()), Toast.LENGTH_LONG).show();
                });
    }

    private void routeUser(RegistrationRequest request) {
        RequestStatus status = request.getStatusEnum();
        UserRole role = request.getRoleEnum();

        if (status == RequestStatus.APPROVED) {
            Toast.makeText(this, R.string.login_success, Toast.LENGTH_SHORT).show();
            Intent intent = role == UserRole.STUDENT
                    ? new Intent(this, StudentHome.class)
                    : new Intent(this, TutorHome.class);
            startActivity(intent);
            finish();
        } else if (status == RequestStatus.REJECTED) {
            startActivity(new Intent(this, RejectedPage.class));
            finish();
        } else {
            startActivity(new Intent(this, PendingPage.class));
            finish();
        }
    }

    private String text(TextInputEditText input) {
        return input.getText() == null ? "" : input.getText().toString().trim();
    }
}
