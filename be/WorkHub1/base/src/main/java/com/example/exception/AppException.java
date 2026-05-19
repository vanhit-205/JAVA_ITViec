package com.example.exception;

public class AppException extends RuntimeException {

    private final int code;
    private final String message;

    public AppException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}