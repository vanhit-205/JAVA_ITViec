package com.example.mapper;

import com.example.domain.dto.request.JobCreateRequest;
import com.example.domain.dto.request.JobUpdateRequest;
import com.example.domain.dto.response.JobResponse;
import com.example.domain.dto.response.SkillResponse;
import com.example.domain.entity.Job;
import com.example.domain.entity.Skill;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class JobMapper {

    public JobResponse toDto(Job job) {
        if (job == null) return null;

        List<SkillResponse> skillResponses = null;
        if (job.skills != null && !job.skills.isEmpty()) {
            skillResponses = job.skills.stream()
                    .map(this::toSkillResponse)
                    .collect(Collectors.toList());
        }

        return JobResponse.builder()
                .id(job.id)
                .name(job.name)
                .location(job.location)
                .salary(job.salary)
                .quantity(job.quantity)
                .level(job.level)
                .description(job.description)
                .startDate(job.startDate)
                .endDate(job.endDate)
                .status(determineStatus(job))
                .deleted(job.deleted)
                .companyId(job.company != null ? job.company.id : null)
                .companyName(job.company != null ? job.company.name : null)
                .companyLogo(job.company != null ? job.company.logo : null)
                .skills(skillResponses)
                .createdAt(job.createdAt)
                .updatedAt(job.updatedAt)
                .createdBy(job.createdBy)
                .updatedBy(job.updatedBy)
                .build();
    }

    public List<JobResponse> toDtoList(List<Job> jobs) {
        if (jobs == null) return null;
        return jobs.stream().map(this::toDto).collect(Collectors.toList());
    }

    public Job toEntity(JobCreateRequest request) {
        if (request == null) return null;
        Job job = new Job();
        job.name = request.name;
        job.location = request.location;
        job.salary = request.salary;
        job.quantity = request.quantity;
        job.level = request.level;
        job.description = request.description;
        job.startDate = request.startDate;
        job.endDate = request.endDate;
        return job;
    }

    public void updateEntity(Job job, JobUpdateRequest request) {
        if (request == null || job == null) return;
        if (request.name != null) job.name = request.name;
        if (request.location != null) job.location = request.location;
        if (request.salary != null) job.salary = request.salary;
        if (request.quantity != null) job.quantity = request.quantity;
        if (request.level != null) job.level = request.level;
        if (request.description != null) job.description = request.description;
        if (request.startDate != null) job.startDate = request.startDate;
        if (request.endDate != null) job.endDate = request.endDate;
    }

    private String determineStatus(Job job) {
        if (job.deleted) {
            return "DELETED";
        }
        if (job.endDate != null && job.endDate.isBefore(java.time.Instant.now())) {
            return "CLOSED";
        }
        return "OPEN";
    }

    private SkillResponse toSkillResponse(Skill skill) {
        SkillResponse response = new SkillResponse();
        response.id = skill.id;
        response.name = skill.name;
        response.level = skill.level;
        response.deleted = skill.deleted;
        response.createdAt = skill.createdAt;
        response.updatedAt = skill.updatedAt;
        response.createdBy = skill.createdBy;
        response.updatedBy = skill.updatedBy;
        return response;
    }
}
