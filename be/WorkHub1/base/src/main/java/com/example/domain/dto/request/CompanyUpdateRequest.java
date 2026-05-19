package com.example.domain.dto.request;

import jakarta.validation.constraints.Size;

/**
 * Company Update Request DTO
 */
public class CompanyUpdateRequest {

    @Size(min = 2, max = 255, message = "Company name must be between 2 and 255 characters")
    public String name;

    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    public String description;

    @Size(max = 500, message = "Address must not exceed 500 characters")
    public String address;

    @Size(max = 255, message = "Logo URL must not exceed 255 characters")
    public String logo;
}