package com.example.otams.data;

import androidx.annotation.Nullable;

import com.example.otams.model.Administrator;
import com.example.otams.model.RegistrationRequest;
import com.example.otams.model.Student;
import com.example.otams.model.Tutor;
import com.example.otams.model.User;
import com.example.otams.model.UserRole;

/**
 * Factory responsible for instantiating {@link User} objects from a {@link RegistrationRequest}.
 */
public final class UserFactory {

    private UserFactory() {
        // Utility class
    }

    @Nullable
    public static User fromRequest(RegistrationRequest request) {
        if (request == null) {
            return null;
        }

        UserRole role = request.getRoleEnum();
        if (role == null) {
            return null;
        }

        User user;
        switch (role) {
            case STUDENT:
                Student student = new Student(request.getEmail());
                student.setProgram(request.getProgram());
                user = student;
                break;
            case TUTOR:
                Tutor tutor = new Tutor(request.getEmail());
                tutor.setDegree(request.getDegree());
                tutor.setCourses(request.getCourses());
                user = tutor;
                break;
            case ADMINISTRATOR:
                user = new Administrator(request.getEmail());
                break;
            default:
                return null;
        }

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setRole(role);
        user.setStatus(request.getStatus());
        return user;
    }
}
