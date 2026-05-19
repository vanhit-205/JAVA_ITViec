package com.example.domain.dto.response;

import java.time.Instant;
import java.util.List;

public class SubscriberResponse {

    public Long id;
    public String name;
    public String email;
    public Boolean enabled;
    public Boolean deleted;
    public List<SkillResponse> skills;
    public Instant lastSentAt;
    public int emailSentCount;
    public Instant createdAt;
    public Instant updatedAt;
    public Long createdBy;
    public Long updatedBy;

    public SubscriberResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public List<SkillResponse> getSkills() {
        return skills;
    }

    public void setSkills(List<SkillResponse> skills) {
        this.skills = skills;
    }

    public Instant getLastSentAt() {
        return lastSentAt;
    }

    public void setLastSentAt(Instant lastSentAt) {
        this.lastSentAt = lastSentAt;
    }

    public int getEmailSentCount() {
        return emailSentCount;
    }

    public void setEmailSentCount(int emailSentCount) {
        this.emailSentCount = emailSentCount;
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

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final SubscriberResponse response = new SubscriberResponse();

        public Builder id(Long id) {
            response.id = id;
            return this;
        }

        public Builder name(String name) {
            response.name = name;
            return this;
        }

        public Builder email(String email) {
            response.email = email;
            return this;
        }

        public Builder enabled(Boolean enabled) {
            response.enabled = enabled;
            return this;
        }

        public Builder deleted(Boolean deleted) {
            response.deleted = deleted;
            return this;
        }

        public Builder skills(List<SkillResponse> skills) {
            response.skills = skills;
            return this;
        }

        public Builder lastSentAt(Instant lastSentAt) {
            response.lastSentAt = lastSentAt;
            return this;
        }

        public Builder emailSentCount(int emailSentCount) {
            response.emailSentCount = emailSentCount;
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

        public SubscriberResponse build() {
            return response;
        }
    }
}