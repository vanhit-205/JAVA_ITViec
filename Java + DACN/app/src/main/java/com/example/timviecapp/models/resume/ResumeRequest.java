package com.example.timviecapp.models.resume;

public class ResumeRequest {
    private String email;
    private String url;
    private int userId;
    private int jobId;

    public ResumeRequest(String email, String url, int userId, int jobId) {
        this.email = email;
        this.url = url;
        this.userId = userId;
        this.jobId = jobId;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public int getJobId() { return jobId; }
    public void setJobId(int jobId) { this.jobId = jobId; }
}
