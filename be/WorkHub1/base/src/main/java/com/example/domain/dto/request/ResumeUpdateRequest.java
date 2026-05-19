package com.example.domain.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;

public class ResumeUpdateRequest {

    @Email(message = "Email khong dung dinh dang")
    public String email;

    public String url;

    public ResumeUpdateRequest() {
    }

    public ResumeUpdateRequest(String email, String url) {
        this.email = email;
        this.url = url;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
