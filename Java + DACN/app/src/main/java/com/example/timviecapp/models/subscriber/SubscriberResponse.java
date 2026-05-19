package com.example.timviecapp.models.subscriber;

import com.example.timviecapp.models.skill.SkillResponse;
import java.util.List;

public class SubscriberResponse {
    private int id;
    private String email;
    private String name;
    private List<SkillResponse> skills;
    private boolean active;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<SkillResponse> getSkills() { return skills; }
    public void setSkills(List<SkillResponse> skills) { this.skills = skills; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
