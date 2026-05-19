package com.example.domain.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public class SubscriberCreateRequest {

    @NotBlank(message = "Name khong duoc de trong")
    public String name;

    @NotBlank(message = "Email khong duoc de trong")
    @Email(message = "Email khong dung dinh dang")
    public String email;

    public List<Long> skillIds;

    public SubscriberCreateRequest() {
    }

    public SubscriberCreateRequest(String name, String email, List<Long> skillIds) {
        this.name = name;
        this.email = email;
        this.skillIds = skillIds;
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
}