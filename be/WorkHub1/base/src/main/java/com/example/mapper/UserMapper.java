package com.example.mapper;

import com.example.domain.dto.request.UserCreateRequest;
import com.example.domain.dto.request.UserUpdateRequest;
import com.example.domain.dto.response.UserResponse;
import com.example.domain.entity.User;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class UserMapper {

    public UserResponse toDto(User user) {
        if (user == null) return null;

        return UserResponse.builder()
                .id(user.id)
                .username(user.username)
                .email(user.email)
                .age(user.age)
                .phone(user.phone)
                .gender(user.gender)
                .dob(user.dob)
                .address(user.address)
                .enabled(user.enabled)
                .accountNonLocked(user.accountNonLocked)
                .deleted(user.deleted)
                .role(user.role != null ? user.role.name : null)
                .companyId(user.company != null ? user.company.id : null)
                .companyName(user.company != null ? user.company.name : null)
                .createdAt(user.createdAt)
                .updatedAt(user.updatedAt)
                .createdBy(user.createdBy)
                .updatedBy(user.updatedBy)
                .build();
    }

    public List<UserResponse> toDtoList(List<User> users) {
        if (users == null) return null;
        return users.stream().map(this::toDto).collect(Collectors.toList());
    }

    public User toEntity(UserCreateRequest request) {
        if (request == null) return null;
        User user = new User();
        user.username = request.username;
        user.email = request.email;
        user.age = request.age;
        user.phone = request.phone;
        user.gender = request.gender;
        user.dob = request.dob;
        user.address = request.address;
        return user;
    }

    public void updateEntity(User user, UserUpdateRequest request) {
        if (request == null || user == null) return;
        if (request.username != null) user.username = request.username;
        if (request.email != null) user.email = request.email;
        if (request.age != null) user.age = request.age;
        if (request.phone != null) user.phone = request.phone;
        if (request.gender != null) user.gender = request.gender;
        if (request.dob != null) user.dob = request.dob;
        if (request.address != null) user.address = request.address;
    }
}
