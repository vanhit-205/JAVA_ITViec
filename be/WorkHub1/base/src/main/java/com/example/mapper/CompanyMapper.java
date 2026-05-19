package com.example.mapper;

import com.example.domain.dto.request.CompanyCreateRequest;
import com.example.domain.dto.request.CompanyUpdateRequest;
import com.example.domain.dto.response.CompanyResponse;
import com.example.domain.entity.Company;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Company Mapper - Manual mapping without MapStruct dependency
 *
 * NOTE: MapStruct requires annotation processing and additional setup.
 * For simplicity, we use manual mapping here. If you want MapStruct:
 * 1. Add dependency: quarkus-hibernate-validator-processor
 * 2. Create CompanyMapper interface with @Mapper annotation
 */
@ApplicationScoped
public class CompanyMapper {

    /**
     * Map Company entity to CompanyResponse DTO
     */
    public CompanyResponse toDto(Company company) {
        if (company == null) return null;

        return new CompanyResponse(
                company.id,
                company.name,
                company.description,
                company.address,
                company.logo,
                company.deleted,
                company.createdAt,
                company.updatedAt,
                company.createdBy,
                company.updatedBy
        );
    }

    /**
     * Map list of Company entities to list of CompanyResponse DTOs
     */
    public java.util.List<CompanyResponse> toDtoList(java.util.List<Company> companies) {
        if (companies == null) return null;
        return companies.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Map CompanyCreateRequest DTO to Company entity
     */
    public Company toEntity(CompanyCreateRequest request) {
        if (request == null) return null;

        Company company = new Company();
        company.name = request.name;
        company.description = request.description;
        company.address = request.address;
        company.logo = request.logo;
        return company;
    }

    /**
     * Update existing Company entity from CompanyUpdateRequest DTO
     */
    public void updateEntity(Company company, CompanyUpdateRequest request) {
        if (request == null || company == null) return;

        if (request.name != null) company.name = request.name;
        if (request.description != null) company.description = request.description;
        if (request.address != null) company.address = request.address;
        if (request.logo != null) company.logo = request.logo;
    }
}
