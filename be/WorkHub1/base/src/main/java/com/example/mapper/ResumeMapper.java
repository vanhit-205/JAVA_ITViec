package com.example.mapper;

import com.example.domain.dto.request.ResumeCreateRequest;
import com.example.domain.dto.request.ResumeUpdateRequest;
import com.example.domain.dto.response.ResumeResponse;
import com.example.domain.entity.Resume;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class ResumeMapper {

    @Inject
    SkillMapper skillMapper;

    public ResumeResponse toDto(Resume resume) {
        if (resume == null) return null;

        return ResumeResponse.builder()
                .id(resume.id)
                .email(resume.email)
                .url(resume.url)
                .status(resume.status)
                .deleted(resume.deleted)
                .userId(resume.user != null ? resume.user.id : null)
                .username(resume.user != null ? resume.user.username : null)
                .jobId(resume.job != null ? resume.job.id : null)
                .jobName(resume.job != null ? resume.job.name : null)
                .companyId(resume.job != null && resume.job.company != null ? resume.job.company.id : null)
                .companyName(resume.job != null && resume.job.company != null ? resume.job.company.name : null)
                .skills(resume.skills != null ? skillMapper.toDtoList(resume.skills) : null)
                .createdAt(resume.createdAt)
                .updatedAt(resume.updatedAt)
                .createdBy(resume.createdBy)
                .updatedBy(resume.updatedBy)
                .build();
    }

    public List<ResumeResponse> toDtoList(List<Resume> resumes) {
        if (resumes == null) return null;
        return resumes.stream().map(this::toDto).collect(Collectors.toList());
    }

    public Resume toEntity(ResumeCreateRequest request) {
        if (request == null) return null;
        Resume resume = new Resume();
        resume.email = request.email;
        resume.url = request.url;
        return resume;
    }

    public void updateEntity(Resume resume, ResumeUpdateRequest request) {
        if (request == null || resume == null) return;
        if (request.email != null) resume.email = request.email;
        if (request.url != null) resume.url = request.url;
    }
}
