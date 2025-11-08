package com.example.otams.model;

import androidx.annotation.Nullable;

import com.google.firebase.firestore.PropertyName;

import java.util.ArrayList;
import java.util.List;

/**
 * Data model representing a registration request submitted by a user.
 */
public class RegistrationRequest {

    private String requestId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String role;
    private String status;
    private String userId;
    private String program;
    private String degree;
    private List<String> courses = new ArrayList<>();
    private String password;

    public RegistrationRequest() {
        setStatus(RequestStatus.PENDING);
    }

    public RegistrationRequest(String firstName, String lastName, String email, String password,
                                String phoneNumber, UserRole role) {
        this();
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        setRole(role);
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
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

    @PropertyName("email")
    public String getEmail() {
        return email;
    }

    @PropertyName("email")
    public void setEmail(String email) {
        this.email = email;
    }

    @PropertyName("phoneNumber")
    public String getPhoneNumber() {
        return phoneNumber;
    }

    @PropertyName("phoneNumber")
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @PropertyName("role")
    public String getRole() {
        return role;
    }

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

    @PropertyName("status")
    public String getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status == null ? null : status.getFirestoreValue();
    }

    @PropertyName("status")
    public void setStatus(String status) {
        this.status = status;
    }

    @Nullable
    public RequestStatus getStatusEnum() {
        return RequestStatus.fromValue(status);
    }

    @PropertyName("userId")
    public String getUserId() {
        return userId;
    }

    @PropertyName("userId")
    public void setUserId(String userId) {
        this.userId = userId;
    }

    @PropertyName("program")
    public String getProgram() {
        return program;
    }

    @PropertyName("program")
    public void setProgram(String program) {
        this.program = program;
    }

    @PropertyName("degree")
    public String getDegree() {
        return degree;
    }

    @PropertyName("degree")
    public void setDegree(String degree) {
        this.degree = degree;
    }

    @PropertyName("courses")
    public List<String> getCourses() {
        return courses;
    }

    @PropertyName("courses")
    public void setCourses(List<String> courses) {
        this.courses = courses != null ? new ArrayList<>(courses) : new ArrayList<>();
    }

    @PropertyName("password")
    public String getPassword() {
        return password;
    }

    @PropertyName("password")
    public void setPassword(String password) {
        this.password = password;
    }

    public String getCoursesAsString() {
        if (courses == null || courses.isEmpty()) {
            return "N/A";
        }
        return String.join(", ", courses);
    }
}
