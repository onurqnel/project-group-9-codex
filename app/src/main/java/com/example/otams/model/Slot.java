package com.example.otams.model;

/**
 * Represents an available tutoring slot.
 */
public class Slot {

    private Tutor tutor;
    private int startTime;
    private int endTime;
    private int date;

    public Slot() {
        // Required for Firestore.
    }

    public Slot(Tutor tutor) {
        this.tutor = tutor;
    }

    public Slot(Tutor tutor, int start, int end, int date) {
        this.tutor = tutor;
        this.startTime = start;
        this.endTime = end;
        this.date = date;
    }

    public void setEndTime(int time) {
        this.endTime = time;
    }

    public void setStartTime(int time) {
        this.startTime = time;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public int getEndTime() {
        return endTime;
    }

    public int getStartTime() {
        return startTime;
    }

    public int getDate() {
        return date;
    }

    public Tutor getTutor() {
        return tutor;
    }

    public boolean validateTimes() {
        if (startTime < 0 || endTime < 0) {
            throw new IndexOutOfBoundsException("Time doesn't exist.");
        }
        return endTime - startTime == 30;
    }
}
