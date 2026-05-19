package com.example.domain.dto.response;

public class OtpResponse {

    public String message;
    public String email;
    public int expiresInSeconds;

    public OtpResponse() {}

    public OtpResponse(String message, String email, int expiresInSeconds) {
        this.message = message;
        this.email = email;
        this.expiresInSeconds = expiresInSeconds;
    }
}
