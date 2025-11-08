package com.example.otams.model;

/**
 * Represents a scheduled tutoring session.
 */
public class Session extends Slot {

    private String approval;
    private Student student;

    public Session() {
        super();
        this.approval = RequestStatus.PENDING.getFirestoreValue();
    }

    public Session(Tutor tutor, Student student) {
        super(tutor);
        this.student = student;
        this.approval = RequestStatus.PENDING.getFirestoreValue();
    }

    public void setApproval(String approval) {
        this.approval = approval;
    }

    public String getApproval() {
        return this.approval;
    }

    public Student getStudent() {
        return this.student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }
}
