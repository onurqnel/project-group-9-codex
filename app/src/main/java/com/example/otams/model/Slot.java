package com.example.otams.model;

import androidx.annotation.Nullable;

import com.google.firebase.firestore.Exclude;

import java.util.concurrent.TimeUnit;

/**
 * Represents an available tutoring slot.
 */
public class Slot {

    private String id;
    private String tutorId;
    private long startTimeMillis;
    private long endTimeMillis;
    private boolean manualApprovalRequired;

    public Slot() {
        // Required for Firestore.
    }

    public Slot(String tutorId, long startTimeMillis, long endTimeMillis, boolean manualApprovalRequired) {
        this.tutorId = tutorId;
        this.startTimeMillis = startTimeMillis;
        this.endTimeMillis = endTimeMillis;
        this.manualApprovalRequired = manualApprovalRequired;
    }

    public String getTutorId() {
        return tutorId;
    }

    public void setTutorId(String tutorId) {
        this.tutorId = tutorId;
    }

    public long getStartTimeMillis() {
        return startTimeMillis;
    }

    public void setStartTimeMillis(long startTimeMillis) {
        this.startTimeMillis = startTimeMillis;
    }

    public long getEndTimeMillis() {
        return endTimeMillis;
    }

    public void setEndTimeMillis(long endTimeMillis) {
        this.endTimeMillis = endTimeMillis;
    }

    public boolean isManualApprovalRequired() {
        return manualApprovalRequired;
    }

    public void setManualApprovalRequired(boolean manualApprovalRequired) {
        this.manualApprovalRequired = manualApprovalRequired;
    }

    @Exclude
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * Returns whether the provided time range overlaps this slot.
     */
    public boolean overlaps(long startMillis, long endMillis) {
        return startMillis < endTimeMillis && endMillis > startTimeMillis;
    }

    /**
     * Ensures the slot represents a valid 30-minute increment or multiple.
     */
    public boolean isValidDuration() {
        long difference = endTimeMillis - startTimeMillis;
        return difference > 0 && difference % TimeUnit.MINUTES.toMillis(30) == 0;
    }

    /**
     * Convenience for derived classes retrieving status strings.
     */
    @Nullable
    public String getStatusLabel() {
        return null;
    }
}
