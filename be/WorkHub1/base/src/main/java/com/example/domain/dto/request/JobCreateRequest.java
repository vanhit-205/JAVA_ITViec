package com.example.domain.dto.request;

import com.example.constant.LevelEnum;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.List;

public class JobCreateRequest {

    @NotBlank(message = "Job name khong duoc de trong")
    public String name;

    @NotBlank(message = "Location khong duoc de trong")
    public String location;

    @NotNull(message = "Salary khong duoc de trong")
    @Min(value = 1, message = "Salary phai lon hon 0")
    public Double salary;

    @NotNull(message = "Quantity khong duoc de trong")
    @Min(value = 1, message = "Quantity phai lon hon 0")
    public Integer quantity;

    @NotNull(message = "Level khong duoc de trong")
    public LevelEnum level;

    public String description;

    @NotNull(message = "Start date khong duoc de trong")
    public Instant startDate;

    @NotNull(message = "End date khong duoc de trong")
    public Instant endDate;

    @NotNull(message = "Company ID khong duoc de trong")
    public Long companyId;

    public List<Long> skillIds;

    public JobCreateRequest() {
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

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public List<Long> getSkillIds() {
        return skillIds;
    }

    public void setSkillIds(List<Long> skillIds) {
        this.skillIds = skillIds;
    }
}