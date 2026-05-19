package com.example.domain.dto.request;

import com.example.constant.LevelEnum;
import jakarta.validation.constraints.Min;

import java.time.Instant;
import java.util.List;

public class JobUpdateRequest {

    public String name;

    public String location;

    @Min(value = 1, message = "Salary phai lon hon 0")
    public Double salary;

    @Min(value = 1, message = "Quantity phai lon hon 0")
    public Integer quantity;

    public LevelEnum level;

    public String description;

    public Instant startDate;

    public Instant endDate;

    public List<Long> skillIds;

    public JobUpdateRequest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Double getSalary() {
        return salary;
    }

    public void setSalary(Double salary) {
        this.salary = salary;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public LevelEnum getLevel() {
        return level;
    }

    public void setLevel(LevelEnum level) {
        this.level = level;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Instant getStartDate() {
        return startDate;
    }

    public void setStartDate(Instant startDate) {
        this.startDate = startDate;
    }

    public Instant getEndDate() {
        return endDate;
    }

    public void setEndDate(Instant endDate) {
        this.endDate = endDate;
    }

    public List<Long> getSkillIds() {
        return skillIds;
    }

    public void setSkillIds(List<Long> skillIds) {
        this.skillIds = skillIds;
    }
}