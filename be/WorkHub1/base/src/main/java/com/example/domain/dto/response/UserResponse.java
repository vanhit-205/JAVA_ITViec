package com.example.domain.dto.response;

import com.example.constant.GenderEnum;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * User Response DTO - excludes sensitive fields like password
 */
public class UserResponse {

    public Long id;
    public String username;
    public String email;
    public Integer age;
    public String phone;
    public GenderEnum gender;
    public LocalDate dob;
    public String address;
    public boolean enabled;
    public boolean accountNonLocked;
    public boolean deleted;
    public String role;
    public Long companyId;
    public String companyName;
    public LocalDateTime createdAt;
    public LocalDateTime updatedAt;
    public Long createdBy;
    public Long updatedBy;

    public UserResponse() {}

    // Builder pattern for easy construction
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final UserResponse response = new UserResponse();

        public Builder id(Long id) { response.id = id; return this; }
        public Builder username(String username) { response.username = username; return this; }
        public Builder email(String email) { response.email = email; return this; }
        public Builder age(Integer age) { response.age = age; return this; }
        public Builder phone(String phone) { response.phone = phone; return this; }
        public Builder gender(GenderEnum gender) { response.gender = gender; return this; }
        public Builder dob(LocalDate dob) { response.dob = dob; return this; }
        public Builder address(String address) { response.address = address; return this; }
        public Builder enabled(boolean enabled) { response.enabled = enabled; return this; }
        public Builder accountNonLocked(boolean accountNonLocked) { response.accountNonLocked = accountNonLocked; return this; }
        public Builder deleted(boolean deleted) { response.deleted = deleted; return this; }
        public Builder role(String role) { response.role = role; return this; }
        public Builder companyId(Long companyId) { response.companyId = companyId; return this; }
        public Builder companyName(String companyName) { response.companyName = companyName; return this; }
        public Builder createdAt(LocalDateTime createdAt) { response.createdAt = createdAt; return this; }
        public Builder updatedAt(LocalDateTime updatedAt) { response.updatedAt = updatedAt; return this; }
        public Builder createdBy(Long createdBy) { response.createdBy = createdBy; return this; }
        public Builder updatedBy(Long updatedBy) { response.updatedBy = updatedBy; return this; }

        public UserResponse build() { return response; }
    }
}
