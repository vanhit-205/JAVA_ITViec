package com.example.domain.dto.request;

import com.example.constant.GenderEnum;
import jakarta.validation.constraints.*;

public class UserUpdateRequest {

    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    public String username;

    @Email(message = "Invalid email format")
    public String email;

    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    public String newPassword; // Optional - only if changing password

    @Min(value = 18, message = "Age must be at least 18")
    @Max(value = 100, message = "Age must not exceed 100")
    public Integer age;

    @Pattern(regexp = "^[0-9]{10,15}$", message = "Phone must be 10-15 digits")
    public String phone;

    public GenderEnum gender;

    @Past(message = "Date of birth must be in the past")
    public java.time.LocalDate dob;

    @Size(max = 500, message = "Address must not exceed 500 characters")
    public String address;

    public Long companyId; // Optional - for RECRUITER role
}
