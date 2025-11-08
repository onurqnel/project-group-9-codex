package com.example.otams.model;

import androidx.annotation.Nullable;

/**
 * Represents the lifecycle of a scheduled tutoring session.
 */
public enum SessionStatus {
    PENDING("pending", "Pending"),
    APPROVED("approved", "Approved"),
    REJECTED("rejected", "Rejected"),
    CANCELLED("cancelled", "Cancelled"),
    COMPLETED("completed", "Completed");

    private final String firestoreValue;
    private final String displayName;

    SessionStatus(String firestoreValue, String displayName) {
        this.firestoreValue = firestoreValue;
        this.displayName = displayName;
    }

    public String getFirestoreValue() {
        return firestoreValue;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Nullable
    public static SessionStatus fromValue(String value) {
        if (value == null) {
            return null;
        }
        for (SessionStatus status : values()) {
            if (status.firestoreValue.equalsIgnoreCase(value)) {
                return status;
            }
        }
        return null;
    }
}
