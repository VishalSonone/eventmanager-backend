package com.kbcnmu.eventmanager.payload;

import com.kbcnmu.eventmanager.model.Student;

public class LoginResponse {
    private String message;
    private Student student;

    public LoginResponse(String message, Student student) {
        this.message = message;
        this.student = student;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }
}
