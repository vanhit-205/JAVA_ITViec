package com.example.domain.dto.response;

import java.util.List;

public class JobMatchingResponse extends JobResponse {

    public Double matchScore;
    public List<SkillResponse> matchedSkills;
    public List<SkillResponse> missingSkills;

    public JobMatchingResponse() {
        super();
    }

    public JobMatchingResponse(JobResponse jobResponse, Double matchScore, 
                               List<SkillResponse> matchedSkills, List<SkillResponse> missingSkills) {
        this.id = jobResponse.id;
        this.name = jobResponse.name;
        this.location = jobResponse.location;
        this.salary = jobResponse.salary;
        this.quantity = jobResponse.quantity;
        this.level = jobResponse.level;
        this.description = jobResponse.description;
        this.startDate = jobResponse.startDate;
        this.endDate = jobResponse.endDate;
        this.status = jobResponse.status;
        this.deleted = jobResponse.deleted;
        this.companyId = jobResponse.companyId;
        this.companyName = jobResponse.companyName;
        this.companyLogo = jobResponse.companyLogo;
        this.skills = jobResponse.skills;
        this.createdAt = jobResponse.createdAt;
        this.updatedAt = jobResponse.updatedAt;
        this.createdBy = jobResponse.createdBy;
        this.updatedBy = jobResponse.updatedBy;
        this.matchScore = matchScore;
        this.matchedSkills = matchedSkills;
        this.missingSkills = missingSkills;
    }
}
