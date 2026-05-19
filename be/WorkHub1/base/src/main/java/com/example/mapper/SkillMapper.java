package com.example.mapper;

import com.example.domain.dto.request.SkillCreateRequest;
import com.example.domain.dto.request.SkillUpdateRequest;
import com.example.domain.dto.response.SkillResponse;
import com.example.domain.entity.Skill;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class SkillMapper {

    public SkillResponse toDto(Skill skill) {
        if (skill == null) return null;
        return new SkillResponse(
                skill.id,
                skill.name,
                skill.level,
                skill.deleted,
                skill.createdAt,
                skill.updatedAt,
                skill.createdBy,
                skill.updatedBy
        );
    }

    public List<SkillResponse> toDtoList(List<Skill> skills) {
        if (skills == null) return null;
        return skills.stream().map(this::toDto).collect(Collectors.toList());
    }

    public Skill toEntity(SkillCreateRequest request) {
        if (request == null) return null;
        Skill skill = new Skill();
        skill.name = request.name;
        skill.level = request.level;
        return skill;
    }

    public void updateEntity(Skill skill, SkillUpdateRequest request) {
        if (request == null || skill == null) return;
        if (request.name != null) skill.name = request.name;
        if (request.level != null) skill.level = request.level;
    }
}