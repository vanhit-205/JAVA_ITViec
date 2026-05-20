package com.example.domain.dto.response;

import java.time.LocalDateTime;

public class SkillResponse {

    public Long id;
    public String name;
    public String description;
    public String level;
    public boolean deleted;
    public LocalDateTime createdAt;
    public LocalDateTime updatedAt;
    public Long createdBy;
    public Long updatedBy;

    public SkillResponse() {}

    public SkillResponse(Long id, String name, String description, String level, boolean deleted,
                        LocalDateTime createdAt, LocalDateTime updatedAt,
                        Long createdBy, Long updatedBy) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.level = level;
        this.deleted = deleted;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
    }
}
