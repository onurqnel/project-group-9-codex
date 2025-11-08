package com.example.otams.model;

import androidx.annotation.Nullable;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.PropertyName;

/**
 * Base model describing a user profile stored in Firestore.
 */
public abstract class User {

    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String email;
    private String status;
    private String role;

    protected User() {
        // Required for Firestore.
    }

    protected User(String email) {
        this.email = email;
    }

    @PropertyName("status")
    public String getStatus() {
        return status;
    }

    @PropertyName("status")
    public void setStatus(String status) {
        this.status = status;
    }

    @Nullable
    public RequestStatus getStatusEnum() {
        return RequestStatus.fromValue(status);
    }

    @PropertyName("firstName")
    public String getFirstName() {
        return firstName;
    }

    @PropertyName("firstName")
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @PropertyName("lastName")
    public String getLastName() {
        return lastName;
    }

    @PropertyName("lastName")
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @PropertyName("phoneNumber")
    public String getPhoneNumber() {
        return phoneNumber;
    }

    @PropertyName("phoneNumber")
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @PropertyName("email")
    public String getEmail() {
        return email;
    }

    @PropertyName("email")
    public void setEmail(String email) {
        this.email = email;
    }

    @PropertyName("role")
    public String getRole() {
        return role;
    }

    @Exclude
    public void setRole(UserRole role) {
        this.role = role == null ? null : role.getFirestoreValue();
    }

    @PropertyName("role")
    public void setRole(String role) {
        this.role = role;
    }

    @Nullable
    public UserRole getRoleEnum() {
        return UserRole.fromValue(role);
    }
}
