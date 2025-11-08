package com.example.otams.model;

import androidx.annotation.Nullable;

/**
 * Enum capturing the lifecycle of a registration request.
 */
public enum RequestStatus {
    PENDING("pending"),
    APPROVED("approved"),
    REJECTED("rejected");

    private final String firestoreValue;

    RequestStatus(String firestoreValue) {
        this.firestoreValue = firestoreValue;
    }

    public String getFirestoreValue() {
        return firestoreValue;
    }

    @Nullable
    public static RequestStatus fromValue(String value) {
        if (value == null) {
            return null;
        }
        for (RequestStatus status : values()) {
            if (status.firestoreValue.equalsIgnoreCase(value)) {
                return status;
            }
        }
        return null;
    }
}
