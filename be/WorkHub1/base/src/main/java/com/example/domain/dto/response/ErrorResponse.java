package com.example.domain.dto.response;

public class ErrorResponse {

    public int code;
    public String message;
    public String path;
    public long timestamp;

    public ErrorResponse(int code, String message, String path) {
        this.code = code;
        this.message = message;
        this.path = path;
        this.timestamp = System.currentTimeMillis();
    }
}