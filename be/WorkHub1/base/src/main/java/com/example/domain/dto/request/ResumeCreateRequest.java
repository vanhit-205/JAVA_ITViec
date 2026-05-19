package com.example.domain.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.jboss.logging.Logger;

public class ResumeCreateRequest {

    private static final Logger log = Logger.getLogger(ResumeCreateRequest.class);

    @NotBlank(message = "Email khong duoc de trong")
    @Email(message = "Email khong dung dinh dang")
    public String email;

    @NotBlank(message = "URL cv khong duoc de trong")
    public String url;

    @NotNull(message = "Job ID khong duoc de trong")
    public Long jobId;

    public ResumeCreateRequest() {
    }

    public ResumeCreateRequest(String email, String url, Long jobId) {
        this.email = email;
        this.url = url;
        this.jobId = jobId;
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

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }
}
