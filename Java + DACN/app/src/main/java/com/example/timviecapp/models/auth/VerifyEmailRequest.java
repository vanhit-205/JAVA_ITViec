package com.example.timviecapp.models.auth;

public class VerifyEmailRequest {
    private String email;

    public VerifyEmailRequest(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
