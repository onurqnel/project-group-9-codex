package com.example.otams.model;

import androidx.annotation.Nullable;

/**
 * Represents a scheduled tutoring session.
 */
public class Session extends Slot {

    private String studentId;
    private Student student;
    private String status;
    private String courseCode;

    public Session() {
        super();
        this.status = SessionStatus.PENDING.getFirestoreValue();
    }

    public Session(String tutorId, Student student) {
        super();
        setTutorId(tutorId);
        this.student = student;
        this.status = SessionStatus.PENDING.getFirestoreValue();
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public Student getStudent() {
        return this.student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    @Nullable
    public SessionStatus getStatusEnum() {
        return SessionStatus.fromValue(status);
    }

    public void setStatusEnum(SessionStatus status) {
        if (status != null) {
            this.status = status.getFirestoreValue();
        }
    }

    @Override
    public String getStatusLabel() {
        SessionStatus statusEnum = getStatusEnum();
        return statusEnum == null ? null : statusEnum.getDisplayName();
    }
}
