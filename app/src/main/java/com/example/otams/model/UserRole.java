package com.example.otams.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Enum describing the supported user roles in the application.
 */
public enum UserRole {
    STUDENT("Student"),
    TUTOR("Tutor"),
    ADMINISTRATOR("Administrator");

    private final String firestoreValue;

    UserRole(String firestoreValue) {
        this.firestoreValue = firestoreValue;
    }

    /**
     * @return The string value stored in Firestore for this role.
     */
    public String getFirestoreValue() {
        return firestoreValue;
    }

    /**
     * Attempts to resolve a {@link UserRole} from its Firestore string value.
     *
     * @param value persisted value (case sensitive)
     * @return matching role or {@code null} if the value is unknown.
     */
    @Nullable
    public static UserRole fromValue(String value) {
        if (value == null) {
            return null;
        }
        for (UserRole role : values()) {
            if (role.firestoreValue.equalsIgnoreCase(value)) {
                return role;
            }
        }
        return null;
    }

    @NonNull
    @Override
    public String toString() {
        return firestoreValue;
    }
}
