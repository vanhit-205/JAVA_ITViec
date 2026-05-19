package com.example.timviecapp.models.resume;

import com.example.timviecapp.models.auth.UserResponse;
import com.example.timviecapp.models.job.JobResponse;

public class ResumeResponse {
    private int id;
    private String email;
    private String url;
    private String status;
    private UserResponse user;
    private JobResponse job;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public UserResponse getUser() { return user; }
    public void setUser(UserResponse user) { this.user = user; }
    public JobResponse getJob() { return job; }
    public void setJob(JobResponse job) { this.job = job; }
}
