package com.example.timviecapp.models.resume;

public class ResumeStatusRequest {
    private String status;

    public ResumeStatusRequest(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
