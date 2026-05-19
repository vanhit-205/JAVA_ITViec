package com.example.service;

import com.example.constant.ErrorCode;
import com.example.constant.StatusEnum;
import com.example.domain.dto.request.ResumeCreateRequest;
import com.example.domain.dto.request.ResumeStatusUpdateRequest;
import com.example.domain.dto.request.ResumeUpdateRequest;
import com.example.domain.dto.response.ResumeResponse;
import com.example.domain.entity.Job;
import com.example.domain.entity.Resume;
import com.example.domain.entity.User;
import com.example.exception.AppException;
import com.example.filter.FilterParser;
import com.example.filter.ResumeSpecification;
import com.example.mapper.ResumeMapper;
import com.example.pagination.PageRequest;
import com.example.pagination.PageResponse;
import com.example.pagination.PagingMeta;
import com.example.repository.JobRepository;
import com.example.repository.ResumeRepository;
import com.example.repository.UserRepository;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.util.List;

@ApplicationScoped
public class ResumeService {

    private static final Logger log = Logger.getLogger(ResumeService.class);

    @Inject
    ResumeRepository resumeRepository;

    @Inject
    UserRepository userRepository;

    @Inject
    JobRepository jobRepository;

    @Inject
    ResumeMapper resumeMapper;

    @Inject
    FilterParser filterParser;

    @Transactional
    public ResumeResponse create(ResumeCreateRequest request, Long currentUserId) {
        log.info("Creating resume for user: " + currentUserId);

        // Validate user exists
        User user = userRepository.findActiveById(currentUserId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND_FOR_RESUME.code,
                        ErrorCode.USER_NOT_FOUND_FOR_RESUME.message));

        // Validate job exists
        Job job = jobRepository.findActiveById(request.jobId)
                .orElseThrow(() -> new AppException(ErrorCode.JOB_NOT_FOUND.code, ErrorCode.JOB_NOT_FOUND.message));

        // Check duplicate apply - user cannot apply to same job twice
        if (resumeRepository.existsByUserIdAndJobId(currentUserId, request.jobId)) {
            throw new AppException(ErrorCode.RESUME_ALREADY_EXISTS.code, ErrorCode.RESUME_ALREADY_EXISTS.message);
        }

        // Create resume
        Resume resume = resumeMapper.toEntity(request);
        resume.user = user;
        resume.job = job;
        resume.status = StatusEnum.PENDING;
        resume.deleted = false;
        resume.createdBy = currentUserId;
        resume.updatedBy = currentUserId;

        resumeRepository.persist(resume);
        resumeRepository.flush();

        log.info("Resume created with ID: " + resume.id);
        return resumeMapper.toDto(resume);
    }

    public ResumeResponse getById(Long id, Long currentUserId, String currentRole) {
        Resume resume = resumeRepository.findActiveById(id)
                .orElseThrow(
                        () -> new AppException(ErrorCode.RESUME_NOT_FOUND.code, ErrorCode.RESUME_NOT_FOUND.message));

        // Authorization: CANDIDATE can only view own resume, RECRUITER can view resumes
        // for their company jobs
        if ("ROLE_CANDIDATE".equals(currentRole) && !resume.user.id.equals(currentUserId)) {
            throw new AppException(ErrorCode.RESUME_ACCESS_DENIED.code, ErrorCode.RESUME_ACCESS_DENIED.message);
        }

        return resumeMapper.toDto(resume);
    }

    public ResumeResponse getByIdAdmin(Long id) {
        Resume resume = resumeRepository.findActiveById(id)
                .orElseThrow(
                        () -> new AppException(ErrorCode.RESUME_NOT_FOUND.code, ErrorCode.RESUME_NOT_FOUND.message));
        return resumeMapper.toDto(resume);
    }

    @Transactional
    public ResumeResponse update(Long id, ResumeUpdateRequest request, Long currentUserId, String currentRole) {
        log.info("Updating resume: " + id);

        Resume resume = resumeRepository.findActiveById(id)
                .orElseThrow(
                        () -> new AppException(ErrorCode.RESUME_NOT_FOUND.code, ErrorCode.RESUME_NOT_FOUND.message));

        // CANDIDATE can only update their own resume
        if ("ROLE_CANDIDATE".equals(currentRole)) {
            if (!resume.user.id.equals(currentUserId)) {
                throw new AppException(ErrorCode.RESUME_ACCESS_DENIED.code, ErrorCode.RESUME_ACCESS_DENIED.message);
            }
            // Cannot update resume if already APPROVED
            if (resume.status == StatusEnum.APPROVED) {
                throw new AppException(ErrorCode.RESUME_CANNOT_EDIT.code, ErrorCode.RESUME_CANNOT_EDIT.message);
            }
        }

        // ADMIN and RECRUITER can update any resume
        resumeMapper.updateEntity(resume, request);
        resume.updatedBy = currentUserId;
        resumeRepository.persist(resume);

        log.info("Resume updated: " + id);
        return resumeMapper.toDto(resume);
    }

    @Transactional
    public void delete(Long id, Long currentUserId, String currentRole) {
        log.info("Deleting resume: " + id);

        Resume resume = resumeRepository.findActiveById(id)
                .orElseThrow(
                        () -> new AppException(ErrorCode.RESUME_NOT_FOUND.code, ErrorCode.RESUME_NOT_FOUND.message));

        // Authorization: CANDIDATE can only delete own resume, RECRUITER and ADMIN can
        // delete any
        if ("ROLE_CANDIDATE".equals(currentRole) && !resume.user.id.equals(currentUserId)) {
            throw new AppException(ErrorCode.RESUME_ACCESS_DENIED.code, ErrorCode.RESUME_ACCESS_DENIED.message);
        }

        // Soft delete
        resume.softDelete();
        resume.updatedBy = currentUserId;
        resumeRepository.persist(resume);

        log.info("Resume soft deleted: " + id);
    }

    @Transactional
    public ResumeResponse updateStatus(Long id, ResumeStatusUpdateRequest request, Long currentUserId,
            String currentRole) {
        log.info("Updating resume status: " + id + " to " + request.status);

        Resume resume = resumeRepository.findActiveById(id)
                .orElseThrow(
                        () -> new AppException(ErrorCode.RESUME_NOT_FOUND.code, ErrorCode.RESUME_NOT_FOUND.message));

        // Only RECRUITER and ADMIN can update status
        if (!"ROLE_RECRUITER".equals(currentRole) && !"ROLE_ADMIN".equals(currentRole)) {
            throw new AppException(ErrorCode.FORBIDDEN.code, "Only RECRUITER or ADMIN can update resume status");
        }

        // Validate status transition
        validateStatusTransition(resume.status, request.status);

        // Apply status update
        resume.status = request.status;
        resume.updatedBy = currentUserId;
        resumeRepository.persist(resume);

        log.info("Resume status updated: " + id + " -> " + request.status);
        return resumeMapper.toDto(resume);
    }

    private void validateStatusTransition(StatusEnum currentStatus, StatusEnum newStatus) {
        // Valid transitions:
        // PENDING -> REVIEWING, REJECTED
        // REVIEWING -> APPROVED, REJECTED
        // APPROVED -> (no transitions allowed)
        // REJECTED -> REVIEWING (can re-review)

        boolean valid = switch (currentStatus) {
            case PENDING -> newStatus == StatusEnum.REVIEWING || newStatus == StatusEnum.REJECTED;
            case REVIEWING -> newStatus == StatusEnum.APPROVED || newStatus == StatusEnum.REJECTED;
            case APPROVED -> false; // Cannot change approved resume
            case REJECTED -> newStatus == StatusEnum.REVIEWING;
        };

        if (!valid) {
            throw new AppException(ErrorCode.RESUME_INVALID_STATUS.code,
                    "Cannot transition from " + currentStatus + " to " + newStatus);
        }
    }

    public PageResponse<ResumeResponse> getAll(PageRequest pageRequest) {
        log.info("Getting resumes - page: " + pageRequest.getPage() + ", size: " + pageRequest.getSize());

        var filter = filterParser.parse(pageRequest.getFilter());
        var queryResult = ResumeSpecification.buildQuery(filter, pageRequest.getKeyword());
        Sort sort = ResumeSpecification.buildSort(pageRequest.getSortBy(), pageRequest.isAscending());

        int offset = pageRequest.getOffset();
        int limit = pageRequest.getSize();

        List<Resume> resumes = resumeRepository.findWithFilter(
                queryResult.query, queryResult.params, sort, offset, limit);
        long total = resumeRepository.countWithFilter(queryResult.query, queryResult.params);

        PagingMeta meta = new PagingMeta(
                total,
                (int) Math.ceil((double) total / pageRequest.getSize()),
                pageRequest.getPage(),
                pageRequest.getSize(),
                pageRequest.getSortBy(),
                pageRequest.getDirection());

        List<ResumeResponse> items = resumeMapper.toDtoList(resumes);
        return PageResponse.of(meta, items);
    }

    public PageResponse<ResumeResponse> getByUser(Long userId, PageRequest pageRequest) {
        log.info("Getting resumes for user: " + userId);

        var filter = filterParser.parse(pageRequest.getFilter());
        var queryResult = ResumeSpecification.buildQuery(filter, pageRequest.getKeyword());
        Sort sort = ResumeSpecification.buildSort(pageRequest.getSortBy(), pageRequest.isAscending());

        // Build query with user filter
        String userQuery = queryResult.query + " AND user.id = :userId";
        var params = io.quarkus.panache.common.Parameters.with("userId", userId);

        int offset = pageRequest.getOffset();
        int limit = pageRequest.getSize();

        List<Resume> resumes = resumeRepository.findWithFilter(userQuery, params, sort, offset, limit);
        long total = resumeRepository.countWithFilter(userQuery, params);

        PagingMeta meta = new PagingMeta(
                total,
                (int) Math.ceil((double) total / pageRequest.getSize()),
                pageRequest.getPage(),
                pageRequest.getSize(),
                pageRequest.getSortBy(),
                pageRequest.getDirection());

        List<ResumeResponse> items = resumeMapper.toDtoList(resumes);
        return PageResponse.of(meta, items);
    }

    public PageResponse<ResumeResponse> getByCompany(Long companyId, PageRequest pageRequest) {
        log.info("Getting resumes for company: " + companyId);

        var filter = filterParser.parse(pageRequest.getFilter());
        var queryResult = ResumeSpecification.buildQuery(filter, pageRequest.getKeyword());
        Sort sort = ResumeSpecification.buildSort(pageRequest.getSortBy(), pageRequest.isAscending());

        // Build query with company filter
        String companyQuery = queryResult.query + " AND job.company.id = :companyId";
        var params = io.quarkus.panache.common.Parameters.with("companyId", companyId);

        int offset = pageRequest.getOffset();
        int limit = pageRequest.getSize();

        List<Resume> resumes = resumeRepository.findWithFilter(companyQuery, params, sort, offset, limit);
        long total = resumeRepository.countWithFilter(companyQuery, params);

        PagingMeta meta = new PagingMeta(
                total,
                (int) Math.ceil((double) total / pageRequest.getSize()),
                pageRequest.getPage(),
                pageRequest.getSize(),
                pageRequest.getSortBy(),
                pageRequest.getDirection());

        List<ResumeResponse> items = resumeMapper.toDtoList(resumes);
        return PageResponse.of(meta, items);
    }
}