package com.example.otams.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Domain model for an administrator.
 */
public class Administrator extends User {

    private final List<RegistrationRequest> requests = new ArrayList<>();

    public Administrator() {
        super();
        setRole(UserRole.ADMINISTRATOR);
    }

    public Administrator(String email) {
        super(email);
        setRole(UserRole.ADMINISTRATOR);
    }

    public List<RegistrationRequest> getRequests() {
        return requests;
    }

    public void approveAll() {
        for (RegistrationRequest request : requests) {
            request.setStatus(RequestStatus.APPROVED);
        }
    }

    public void denyAll() {
        for (RegistrationRequest request : requests) {
            request.setStatus(RequestStatus.REJECTED);
        }
    }

    public void approve(String userEmail) {
        updateStatus(userEmail, RequestStatus.APPROVED);
    }

    public void deny(String userEmail) {
        updateStatus(userEmail, RequestStatus.REJECTED);
    }

    private void updateStatus(String userEmail, RequestStatus status) {
        for (RegistrationRequest request : requests) {
            if (userEmail != null && userEmail.equals(request.getEmail())) {
                request.setStatus(status);
                return;
            }
        }
    }
}
