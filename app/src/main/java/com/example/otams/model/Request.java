package com.example.otams.model;

/**
 * Represents a session request sent to a tutor.
 */
public class Request {

    private User user;
    private boolean approval;

    public Request() {
        // Required for Firestore.
    }

    public Request(User user) {
        this.user = user;
        this.approval = false;
    }

    public void setApprovalApproved() {
        this.approval = true;
    }

    public void setApprovalNotApproved() {
        this.approval = false;
    }

    public boolean getApproval() {
        return this.approval;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
