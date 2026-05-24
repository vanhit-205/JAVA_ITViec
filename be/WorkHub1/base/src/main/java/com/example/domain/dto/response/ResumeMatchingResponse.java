package com.example.domain.dto.response;

import java.util.List;

public class ResumeMatchingResponse extends ResumeResponse {

    public Double matchScore;
    public List<SkillResponse> matchedSkills;
    public List<SkillResponse> missingSkills;

    public ResumeMatchingResponse() {
        super();
    }

    public ResumeMatchingResponse(ResumeResponse resumeResponse, Double matchScore, 
                                  List<SkillResponse> matchedSkills, List<SkillResponse> missingSkills) {
        this.id = resumeResponse.id;
        this.email = resumeResponse.email;
        this.url = resumeResponse.url;
        this.status = resumeResponse.status;
        this.deleted = resumeResponse.deleted;
        this.userId = resumeResponse.userId;
        this.username = resumeResponse.username;
        this.jobId = resumeResponse.jobId;
        this.jobName = resumeResponse.jobName;
        this.companyId = resumeResponse.companyId;
        this.companyName = resumeResponse.companyName;
        this.createdAt = resumeResponse.createdAt;
        this.updatedAt = resumeResponse.updatedAt;
        this.createdBy = resumeResponse.createdBy;
        this.updatedBy = resumeResponse.updatedBy;
        this.skills = resumeResponse.skills;
        this.matchScore = matchScore;
        this.matchedSkills = matchedSkills;
        this.missingSkills = missingSkills;
    }
}
