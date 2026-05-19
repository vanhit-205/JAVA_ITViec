package com.example.domain.dto.response;

import com.example.constant.LevelEnum;

import java.time.Instant;
import java.util.List;

public class JobResponse {

    public Long id;
    public String name;
    public String location;
    public Double salary;
    public Integer quantity;
    public LevelEnum level;
    public String description;
    public Instant startDate;
    public Instant endDate;
    public String status; // OPEN, CLOSED
    public Boolean deleted;
    public Long companyId;
    public String companyName;
    public String companyLogo;
    public List<SkillResponse> skills;
    public Instant createdAt;
    public Instant updatedAt;
    public Long createdBy;
    public Long updatedBy;

    public JobResponse() {
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
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

    public String getCompanyLogo() {
        return companyLogo;
    }

    public void setCompanyLogo(String companyLogo) {
        this.companyLogo = companyLogo;
    }

    public List<SkillResponse> getSkills() {
        return skills;
    }

    public void setSkills(List<SkillResponse> skills) {
        this.skills = skills;
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
        private final JobResponse response = new JobResponse();

        public Builder id(Long id) {
            response.id = id;
            return this;
        }

        public Builder name(String name) {
            response.name = name;
            return this;
        }

        public Builder location(String location) {
            response.location = location;
            return this;
        }

        public Builder salary(Double salary) {
            response.salary = salary;
            return this;
        }

        public Builder quantity(Integer quantity) {
            response.quantity = quantity;
            return this;
        }

        public Builder level(LevelEnum level) {
            response.level = level;
            return this;
        }

        public Builder description(String description) {
            response.description = description;
            return this;
        }

        public Builder startDate(Instant startDate) {
            response.startDate = startDate;
            return this;
        }

        public Builder endDate(Instant endDate) {
            response.endDate = endDate;
            return this;
        }

        public Builder status(String status) {
            response.status = status;
            return this;
        }

        public Builder deleted(Boolean deleted) {
            response.deleted = deleted;
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

        public Builder companyLogo(String companyLogo) {
            response.companyLogo = companyLogo;
            return this;
        }

        public Builder skills(List<SkillResponse> skills) {
            response.skills = skills;
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

        public JobResponse build() {
            return response;
        }
    }
}