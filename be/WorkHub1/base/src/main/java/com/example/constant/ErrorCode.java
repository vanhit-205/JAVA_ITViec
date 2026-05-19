package com.example.constant;

public enum ErrorCode {

    USER_NOT_FOUND(1001, "User not found"),
    INVALID_INPUT(1002, "Invalid input"),
    UNAUTHORIZED(1003, "Unauthorized - Invalid credentials"),
    FORBIDDEN(1004, "Forbidden - Access denied"),
    INTERNAL_ERROR(9999, "Internal server error"),

    // Auth specific
    EMAIL_NOT_FOUND(2001, "Email not found"),
    INVALID_PASSWORD(2002, "Invalid password"),
    USERNAME_EXISTS(2003, "Username already exists"),
    EMAIL_EXISTS(2004, "Email already exists"),
    ROLE_NOT_FOUND(2005, "Role not found"),
    TOKEN_EXPIRED(2006, "Token expired"),
    TOKEN_INVALID(2007, "Token invalid"),
    TOKEN_BLACKLISTED(2008, "Token has been revoked"),
    SESSION_EXPIRED(2009, "Session expired"),
    ACCOUNT_DISABLED(2010, "Account is disabled"),
    ACCOUNT_LOCKED(2011, "Account is locked"),

    // Forgot Password specific
    OTP_EXPIRED(3001, "OTP has expired"),
    OTP_INVALID(3002, "Invalid OTP"),
    OTP_ALREADY_USED(3003, "OTP has already been used"),
    OTP_RATE_LIMITED(3004, "Please wait before requesting another OTP"),
    INVALID_REPEAT_PASSWORD(3005, "Passwords do not match"),
    EMAIL_NOT_FOUND_FOR_RESET(3006, "Email not found in our system"),

    // Company specific
    COMPANY_NOT_FOUND(4001, "Company not found"),
    COMPANY_ALREADY_EXISTS(4002, "Company with this name already exists"),

    // Skill specific
    SKILL_NOT_FOUND(5001, "Skill not found"),
    SKILL_ALREADY_EXISTS(5002, "Skill with this name already exists"),

    // Resume specific
    RESUME_NOT_FOUND(6001, "Resume not found"),
    RESUME_ALREADY_EXISTS(6002, "Resume already exists for this job"),
    RESUME_CANNOT_EDIT(6003, "Cannot edit resume with APPROVED status"),
    RESUME_ACCESS_DENIED(6004, "Access denied to this resume"),
    RESUME_INVALID_STATUS(6005, "Invalid status transition"),
    JOB_NOT_FOUND(6006, "Job not found"),
    USER_NOT_FOUND_FOR_RESUME(6007, "User not found"),

    // Job specific
    JOB_ALREADY_EXISTS(7001, "Job with this name already exists"),
    JOB_CLOSED(7002, "Cannot edit closed job"),
    JOB_INVALID_DATE(7003, "End date must be after start date"),
    JOB_ACCESS_DENIED(7004, "Access denied to this job"),
    COMPANY_NOT_FOUND_FOR_JOB(7005, "Company not found"),
    SKILL_NOT_FOUND_FOR_JOB(7006, "Skill not found"),
    JOB_CANNOT_REOPEN(7007, "Cannot reopen deleted job"),

    // Subscriber specific
    SUBSCRIBER_NOT_FOUND(8001, "Subscriber not found"),
    SUBSCRIBER_EMAIL_EXISTS(8002, "Email already subscribed"),
    SUBSCRIBER_DISABLED(8003, "Subscriber is disabled"),
    SKILL_NOT_FOUND_FOR_SUBSCRIBER(8004, "Skill not found"),
    SUBSCRIBER_CANNOT_SEND_EMAIL(8005, "Cannot send email to disabled subscriber");

    public final int code;
    public final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
