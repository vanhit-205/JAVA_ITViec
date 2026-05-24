package com.example.domain.dto.response;

import com.example.constant.StatusEnum;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class ResumeResponse {

    public Long id;
    public String email;
    public String url;
    public StatusEnum status;
    public Boolean deleted;
    public Long userId;
    public String username;
    public Long jobId;
    public String jobName;
    public Long companyId;
    public String companyName;
    public Instant createdAt;
    public Instant updatedAt;
    public Long createdBy;
    public Long updatedBy;
    public List<SkillResponse> skills = new ArrayList<>();

    public ResumeResponse() {
    }

    public ResumeResponse(Long id, String email, String url, StatusEnum status) {
        this.id = id;
        this.email = email;
        this.url = url;
        this.status = status;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public StatusEnum getStatus() {
        return status;
    }

    public void setStatus(StatusEnum status) {
        this.status = status;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public Long getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Long updatedBy) {
        this.updatedBy = updatedBy;
    }

    public List<SkillResponse> getSkills() {
        return skills;
    }

    public void setSkills(List<SkillResponse> skills) {
        this.skills = skills;
    }

    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final ResumeResponse response = new ResumeResponse();

        public Builder id(Long id) {
            response.id = id;
            return this;
        }

        public Builder email(String email) {
            response.email = email;
            return this;
        }

        public Builder url(String url) {
            response.url = url;
            return this;
        }

        public Builder status(StatusEnum status) {
            response.status = status;
            return this;
        }

        public Builder deleted(Boolean deleted) {
            response.deleted = deleted;
            return this;
        }

        public Builder userId(Long userId) {
            response.userId = userId;
            return this;
        }

        public Builder username(String username) {
            response.username = username;
            return this;
        }

        public Builder jobId(Long jobId) {
            response.jobId = jobId;
            return this;
        }

        public Builder jobName(String jobName) {
            response.jobName = jobName;
            return this;
        }

        public Builder companyId(Long companyId) {
            response.companyId = companyId;
            return this;
        }

        public Builder companyName(String companyName) {
            response.companyName = companyName;
            return this;
        }

        public Builder createdAt(Instant createdAt) {
            response.createdAt = createdAt;
            return this;
        }

        public Builder updatedAt(Instant updatedAt) {
            response.updatedAt = updatedAt;
            return this;
        }

        public Builder createdBy(Long createdBy) {
            response.createdBy = createdBy;
            return this;
        }

        public Builder updatedBy(Long updatedBy) {
            response.updatedBy = updatedBy;
            return this;
        }

        public Builder skills(List<SkillResponse> skills) {
            response.skills = skills;
            return this;
        }

        public ResumeResponse build() {
            return response;
        }
    }
}
