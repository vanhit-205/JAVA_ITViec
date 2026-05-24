package com.example.timviecapp.models.job;

import com.example.timviecapp.models.company.CompanyResponse;
import com.example.timviecapp.models.skill.SkillResponse;
import java.util.List;

public class JobResponse {
    private int id;
    private String name;
    private String location;
    private double salary;
    private int quantity;
    private String level;
    private String description;
    private String startDate;
    private String endDate;
    private boolean active;
    private String status;
    private CompanyResponse company;
    private List<SkillResponse> skills;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public double getSalary() { return salary; }
    public void setSalary(double salary) { this.salary = salary; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }
    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }
    
    public boolean isActive() { 
        if (status != null) {
            return "OPEN".equalsIgnoreCase(status);
        }
        
        // Dự phòng: So sánh hạn nộp (endDate) với thời gian hiện tại
        if (endDate != null && !endDate.isEmpty()) {
            try {
                java.text.SimpleDateFormat parser = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.getDefault());
                parser.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
                java.util.Date end = parser.parse(endDate);
                return end.after(new java.util.Date());
            } catch (Exception e) {
                try {
                    java.text.SimpleDateFormat parser = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
                    java.util.Date end = parser.parse(endDate);
                    return end.after(new java.util.Date());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        return active; 
    }
    public void setActive(boolean active) { this.active = active; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public CompanyResponse getCompany() { return company; }
    public void setCompany(CompanyResponse company) { this.company = company; }
    public List<SkillResponse> getSkills() { return skills; }
    public void setSkills(List<SkillResponse> skills) { this.skills = skills; }
}
