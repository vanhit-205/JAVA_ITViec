package com.example.mapper;

import com.example.domain.dto.request.SubscriberCreateRequest;
import com.example.domain.dto.request.SubscriberUpdateRequest;
import com.example.domain.dto.response.SubscriberResponse;
import com.example.domain.dto.response.SkillResponse;
import com.example.domain.entity.Skill;
import com.example.domain.entity.Subscriber;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class SubscriberMapper {

    public SubscriberResponse toDto(Subscriber subscriber) {
        if (subscriber == null) return null;

        List<SkillResponse> skillResponses = null;
        if (subscriber.skills != null && !subscriber.skills.isEmpty()) {
            skillResponses = subscriber.skills.stream()
                    .map(this::toSkillResponse)
                    .collect(Collectors.toList());
        }

        return SubscriberResponse.builder()
                .id(subscriber.id)
                .name(subscriber.name)
                .email(subscriber.email)
                .enabled(subscriber.enabled)
                .deleted(subscriber.deleted)
                .skills(skillResponses)
                .lastSentAt(subscriber.lastSentAt)
                .emailSentCount(subscriber.emailSentCount)
                .createdAt(subscriber.createdAt)
                .updatedAt(subscriber.updatedAt)
                .createdBy(subscriber.createdBy)
                .updatedBy(subscriber.updatedBy)
                .build();
    }

    public List<SubscriberResponse> toDtoList(List<Subscriber> subscribers) {
        if (subscribers == null) return null;
        return subscribers.stream().map(this::toDto).collect(Collectors.toList());
    }

    public Subscriber toEntity(SubscriberCreateRequest request) {
        if (request == null) return null;
        Subscriber subscriber = new Subscriber();
        subscriber.name = request.name;
        subscriber.email = request.email;
        return subscriber;
    }

    public void updateEntity(Subscriber subscriber, SubscriberUpdateRequest request) {
        if (request == null || subscriber == null) return;
        if (request.name != null) subscriber.name = request.name;
        if (request.email != null) subscriber.email = request.email;
        if (request.enabled != null) {
            if (request.enabled) {
                subscriber.enable();
            } else {
                subscriber.disable();
            }
        }
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