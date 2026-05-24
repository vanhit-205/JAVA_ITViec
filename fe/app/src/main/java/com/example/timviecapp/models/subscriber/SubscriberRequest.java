package com.example.timviecapp.models.subscriber;

import java.util.List;

public class SubscriberRequest {
    private String email;
    private String name;
    private List<Integer> skillIds;

    public SubscriberRequest(String email, String name, List<Integer> skillIds) {
        this.email = email;
        this.name = name;
        this.skillIds = skillIds;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<Integer> getSkillIds() { return skillIds; }
    public void setSkillIds(List<Integer> skillIds) { this.skillIds = skillIds; }
}
