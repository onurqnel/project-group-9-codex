package com.example.otams.ui.registration;

import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.otams.R;
import com.example.otams.model.RegistrationRequest;
import com.example.otams.model.UserRole;
import com.example.otams.ui.common.BaseRegistrationActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseUser;

public class StudentRegistrationPage extends BaseRegistrationActivity {

    private TextInputEditText firstNameInput;
    private TextInputEditText lastNameInput;
    private TextInputEditText phoneInput;
    private TextInputEditText programInput;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_student_registration;
    }

    @Override
    protected int getProgressBarId() {
        return R.id.progressBar;
    }

    @Override
    protected int getSignUpButtonId() {
        return R.id.signUpButton;
    }

    @Override
    protected int getLogoutButtonId() {
        return R.id.logoutButton;
    }

    @Override
    protected void onFormCreated(FirebaseUser user) {
        firstNameInput = findViewById(R.id.firstName);
        lastNameInput = findViewById(R.id.lastName);
        phoneInput = findViewById(R.id.phoneNum);
        programInput = findViewById(R.id.programOfStudy);
    }

    @Nullable
    @Override
    protected RegistrationRequest buildRequest(FirebaseUser user) {
        String firstName = text(firstNameInput);
        String lastName = text(lastNameInput);
        String phone = text(phoneInput);
        String program = text(programInput);
        String email = user.getEmail();

        if (firstName.isEmpty() || lastName.isEmpty() || phone.isEmpty() || program.isEmpty() || email == null) {
            Toast.makeText(this, R.string.registration_fill_all_fields, Toast.LENGTH_SHORT).show();
            return null;
        }

        RegistrationRequest request = new RegistrationRequest();
        request.setUserId(user.getUid());
        request.setRole(UserRole.STUDENT);
        request.setFirstName(firstName);
        request.setLastName(lastName);
        request.setPhoneNumber(phone);
        request.setEmail(email);
        request.setProgram(program);
        return request;
    }
}
