package com.example.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class SkillCreateRequest {

    @NotBlank(message = "Skill name is required")
    @Size(min = 2, max = 100, message = "Skill name must be between 2 and 100 characters")
    public String name;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    public String description;

    @Size(max = 50, message = "Level must not exceed 50 characters")
    public String level;
}
