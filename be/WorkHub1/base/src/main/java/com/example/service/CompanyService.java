package com.example.service;

import com.example.constant.ErrorCode;
import com.example.domain.dto.request.CompanyCreateRequest;
import com.example.domain.dto.request.CompanyUpdateRequest;
import com.example.domain.dto.response.CompanyResponse;
import com.example.domain.entity.Company;
import com.example.exception.AppException;
import com.example.filter.CompanySpecification;
import com.example.filter.FilterExpression;
import com.example.filter.FilterParser;
import com.example.filter.SearchCriteria;
import com.example.mapper.CompanyMapper;
import com.example.pagination.PageRequest;
import com.example.pagination.PageResponse;
import com.example.pagination.PagingMeta;
import com.example.repository.CompanyRepository;
import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.util.List;

@ApplicationScoped
public class CompanyService {

    private static final Logger log = Logger.getLogger(CompanyService.class);

    @Inject
    CompanyRepository companyRepository;

    @Inject
    CompanyMapper companyMapper;

    @Inject
    FilterParser filterParser;

    /**
     * Create new company
     */
    @Transactional
    public CompanyResponse create(CompanyCreateRequest request) {
        log.info("Creating company: " + request.name);

        // Check duplicate name
        if (companyRepository.existsByName(request.name)) {
            throw new AppException(ErrorCode.COMPANY_ALREADY_EXISTS.code,
                    ErrorCode.COMPANY_ALREADY_EXISTS.message);
        }

        Company company = companyMapper.toEntity(request);
        companyRepository.persist(company);

        log.info("Company created with ID: " + company.id);
        return companyMapper.toDto(company);
    }

    /**
     * Get company by ID
     */
    public CompanyResponse getById(Long id) {
        Company company = companyRepository.findActiveById(id)
                .orElseThrow(() -> new AppException(ErrorCode.COMPANY_NOT_FOUND.code,
                        ErrorCode.COMPANY_NOT_FOUND.message));
        return companyMapper.toDto(company);
    }

    /**
     * Update company
     */
    @Transactional
    public CompanyResponse update(Long id, CompanyUpdateRequest request) {
        log.info("Updating company: " + id);

        Company company = companyRepository.findActiveById(id)
                .orElseThrow(() -> new AppException(ErrorCode.COMPANY_NOT_FOUND.code,
                        ErrorCode.COMPANY_NOT_FOUND.message));

        // Check duplicate name (exclude current company)
        if (request.name != null && !request.name.equals(company.name)) {
            if (companyRepository.existsByNameAndNotId(request.name, id)) {
                throw new AppException(ErrorCode.COMPANY_ALREADY_EXISTS.code,
                        ErrorCode.COMPANY_ALREADY_EXISTS.message);
            }
        }

        companyMapper.updateEntity(company, request);
        companyRepository.persist(company);

        log.info("Company updated: " + id);
        return companyMapper.toDto(company);
    }

    /**
     * Soft delete company
     */
    @Transactional
    public void delete(Long id) {
        log.info("Deleting company: " + id);

        Company company = companyRepository.findActiveById(id)
                .orElseThrow(() -> new AppException(ErrorCode.COMPANY_NOT_FOUND.code,
                        ErrorCode.COMPANY_NOT_FOUND.message));

        company.softDelete();
        companyRepository.persist(company);

        log.info("Company soft deleted: " + id);
    }

    /**
     * Get paginated list with search and filter
     */
    public PageResponse<CompanyResponse> getAll(PageRequest pageRequest) {
        log.info("Getting companies - page: " + pageRequest.getPage() + ", size: " + pageRequest.getSize());

        // Parse filter
        FilterExpression filter = filterParser.parse(pageRequest.getFilter());

        // Build query
        CompanySpecification.QueryResult queryResult = CompanySpecification.buildQuery(
                filter, pageRequest.getKeyword());

        // Build sort
        Sort sort = CompanySpecification.buildSort(pageRequest.getSortBy(), pageRequest.isAscending());

        // Execute query
        int offset = pageRequest.getOffset();
        int limit = pageRequest.getSize();

        List<Company> companies = companyRepository.findWithFilter(
                queryResult.query, queryResult.params, sort, offset, limit);

        long total = companyRepository.countWithFilter(queryResult.query, queryResult.params);

        // Build meta
        PagingMeta meta = new PagingMeta(
                total,
                (int) Math.ceil((double) total / pageRequest.getSize()),
                pageRequest.getPage(),
                pageRequest.getSize(),
                pageRequest.getSortBy(),
                pageRequest.getDirection()
        );

        // Map to DTO
        List<CompanyResponse> items = companyMapper.toDtoList(companies);

        return PageResponse.of(meta, items);
    }
}