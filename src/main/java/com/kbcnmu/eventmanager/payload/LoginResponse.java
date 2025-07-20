package com.kbcnmu.eventmanager.payload;

import com.kbcnmu.eventmanager.model.Student;

public class LoginResponse {
    private String message;
    private Student student;
    private String status;

    public LoginResponse(String message, Student student) {
        this.message = message;
        this.student = student;
        this.status = "success";
    }

    public LoginResponse(String message) {
        this.message = message;
        this.status = "error";
    }

    public String getMessage() {
        return message;
    }

    public Student getStudent() {
        return student;
    }

    public String getStatus() {
        return status;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
