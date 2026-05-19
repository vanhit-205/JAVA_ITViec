package com.example.domain.dto.request;

import jakarta.validation.constraints.Size;

public class SkillUpdateRequest {

    @Size(min = 2, max = 100, message = "Skill name must be between 2 and 100 characters")
    public String name;

    @Size(max = 50, message = "Level must not exceed 50 characters")
    public String level;
}
