package com.example.domain.dto.request;

import jakarta.validation.constraints.Email;

import java.util.List;

public class SubscriberUpdateRequest {

    public String name;

    @Email(message = "Email khong dung dinh dang")
    public String email;

    public List<Long> skillIds;

    public Boolean enabled;

    public SubscriberUpdateRequest() {
    }

    public SubscriberUpdateRequest(String name, String email, List<Long> skillIds, Boolean enabled) {
        this.name = name;
        this.email = email;
        this.skillIds = skillIds;
        this.enabled = enabled;
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

    public List<Long> getSkillIds() {
        return skillIds;
    }

    public void setSkillIds(List<Long> skillIds) {
        this.skillIds = skillIds;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}