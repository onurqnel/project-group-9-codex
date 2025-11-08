package com.example.otams.ui.registration;

import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.otams.R;
import com.example.otams.model.RegistrationRequest;
import com.example.otams.model.UserRole;
import com.example.otams.ui.common.BaseRegistrationActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class TutorRegistrationPage extends BaseRegistrationActivity {

    private TextInputEditText firstNameInput;
    private TextInputEditText lastNameInput;
    private TextInputEditText phoneInput;
    private TextInputEditText degreeInput;
    private TextInputEditText coursesInput;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_tutor_registration;
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
        firstNameInput = findViewById(R.id.editTextFirstName);
        lastNameInput = findViewById(R.id.editTextLastName);
        phoneInput = findViewById(R.id.editTextPhone);
        degreeInput = findViewById(R.id.editTextDegree);
        coursesInput = findViewById(R.id.editTextCourses);
    }

    @Nullable
    @Override
    protected RegistrationRequest buildRequest(FirebaseUser user) {
        String firstName = text(firstNameInput);
        String lastName = text(lastNameInput);
        String phone = text(phoneInput);
        String degree = text(degreeInput);
        String courses = text(coursesInput);
        String email = user.getEmail();

        if (firstName.isEmpty() || lastName.isEmpty() || phone.isEmpty() || degree.isEmpty() || courses.isEmpty() || email == null) {
            Toast.makeText(this, R.string.registration_fill_all_fields, Toast.LENGTH_SHORT).show();
            return null;
        }

        List<String> coursesList = new ArrayList<>();
        for (String course : courses.split("\\s*,\\s*")) {
            if (!course.trim().isEmpty()) {
                coursesList.add(course.trim());
            }
        }

        RegistrationRequest request = new RegistrationRequest();
        request.setUserId(user.getUid());
        request.setRole(UserRole.TUTOR);
        request.setFirstName(firstName);
        request.setLastName(lastName);
        request.setPhoneNumber(phone);
        request.setEmail(email);
        request.setDegree(degree);
        request.setCourses(coursesList);
        return request;
    }
}
