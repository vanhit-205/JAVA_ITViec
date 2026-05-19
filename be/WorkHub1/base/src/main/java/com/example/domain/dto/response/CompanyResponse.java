package com.example.domain.dto.response;

import java.time.LocalDateTime;

/**
 * Company Response DTO
 */
public class CompanyResponse {

    public Long id;
    public String name;
    public String description;
    public String address;
    public String logo;
    public boolean deleted;
    public LocalDateTime createdAt;
    public LocalDateTime updatedAt;
    public Long createdBy;
    public Long updatedBy;

    public CompanyResponse() {}

    public CompanyResponse(Long id, String name, String description, String address,
                          String logo, boolean deleted, LocalDateTime createdAt,
                          LocalDateTime updatedAt, Long createdBy, Long updatedBy) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.address = address;
        this.logo = logo;
        this.deleted = deleted;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
    }
}