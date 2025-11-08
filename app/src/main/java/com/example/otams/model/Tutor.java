package com.example.otams.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Domain model for a tutor profile.
 */
public class Tutor extends User {

    private List<String> coursesOffered = new ArrayList<>();
    private List<Session> pastSessions = new ArrayList<>();
    private List<Slot> availableSlots = new ArrayList<>();
    private List<Session> upcomingSessions = new ArrayList<>();
    private String degree;

    public Tutor() {
        super();
        setRole(UserRole.TUTOR);
    }

    public Tutor(String email) {
        super(email);
        setRole(UserRole.TUTOR);
    }

    public List<String> getCourses() {
        return coursesOffered;
    }

    public void setCourses(List<String> coursesOffered) {
        this.coursesOffered = coursesOffered != null ? new ArrayList<>(coursesOffered) : new ArrayList<>();
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public void setPastSessions(List<Session> pastSessions) {
        this.pastSessions = pastSessions != null ? new ArrayList<>(pastSessions) : new ArrayList<>();
    }

    public void setUpcomingSessions(List<Session> upcoming) {
        this.upcomingSessions = upcoming != null ? new ArrayList<>(upcoming) : new ArrayList<>();
    }

    public void setAvailableSlots(List<Slot> slots) {
        this.availableSlots = slots != null ? new ArrayList<>(slots) : new ArrayList<>();
    }

    public List<Session> getPastSessions() {
        return pastSessions;
    }

    public List<Session> getUpcomingSessions() {
        return upcomingSessions;
    }

    public List<Slot> getAvailableSlots() {
        return availableSlots;
    }

    public void approveSessionRequest(Session session, SessionStatus status) {
        if (session != null && status != null) {
            session.setStatus(status.getFirestoreValue());
        }
    }

    public void approveAllSessionRequests(List<Session> listOfSessions) {
        if (listOfSessions == null) {
            return;
        }
        for (Session session : listOfSessions) {
            session.setStatus(SessionStatus.APPROVED.getFirestoreValue());
        }
    }

    public void createNewSlot(String tutorId, long start, long end, boolean manualApprovalRequired) {
        Slot slot = new Slot(tutorId, start, end, manualApprovalRequired);
        availableSlots.add(slot);
    }
}
