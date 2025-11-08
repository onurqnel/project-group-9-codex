package com.example.otams.model;

/**
 * Domain model for a student.
 */
public class Student extends User {

    private String program;

    public Student() {
        super();
        setRole(UserRole.STUDENT);
    }

    public Student(String email) {
        super(email);
        setRole(UserRole.STUDENT);
    }

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }
}
