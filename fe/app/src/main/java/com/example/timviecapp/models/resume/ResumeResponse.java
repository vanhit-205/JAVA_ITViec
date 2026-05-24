package com.example.timviecapp.models.resume;

public class ResumeResponse {
    private int id;
    private String email;
    private String url;
    private String status;

    // Flat fields trả về từ backend (không phải lồng nhau)
    private long userId;
    private String username;
    private long jobId;
    private String jobName;
    private long companyId;
    private String companyName;

    private String createdAt;
    private String updatedAt;

    // Kỹ năng và thông tin đối khớp (Giai đoạn 3 & 4)
    private java.util.List<com.example.timviecapp.models.skill.SkillResponse> skills;
    private Double matchScore;
    private java.util.List<com.example.timviecapp.models.skill.SkillResponse> matchedSkills;
    private java.util.List<com.example.timviecapp.models.skill.SkillResponse> missingSkills;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public long getJobId() { return jobId; }
    public void setJobId(long jobId) { this.jobId = jobId; }

    public String getJobName() { return jobName; }
    public void setJobName(String jobName) { this.jobName = jobName; }

    public long getCompanyId() { return companyId; }
    public void setCompanyId(long companyId) { this.companyId = companyId; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    public java.util.List<com.example.timviecapp.models.skill.SkillResponse> getSkills() { return skills; }
    public void setSkills(java.util.List<com.example.timviecapp.models.skill.SkillResponse> skills) { this.skills = skills; }

    public Double getMatchScore() { return matchScore; }
    public void setMatchScore(Double matchScore) { this.matchScore = matchScore; }

    public java.util.List<com.example.timviecapp.models.skill.SkillResponse> getMatchedSkills() { return matchedSkills; }
    public void setMatchedSkills(java.util.List<com.example.timviecapp.models.skill.SkillResponse> matchedSkills) { this.matchedSkills = matchedSkills; }

    public java.util.List<com.example.timviecapp.models.skill.SkillResponse> getMissingSkills() { return missingSkills; }
    public void setMissingSkills(java.util.List<com.example.timviecapp.models.skill.SkillResponse> missingSkills) { this.missingSkills = missingSkills; }
}
