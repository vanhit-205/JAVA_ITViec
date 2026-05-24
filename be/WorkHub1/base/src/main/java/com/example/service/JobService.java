package com.example.service;

import com.example.constant.ErrorCode;
import com.example.domain.dto.request.JobCreateRequest;
import com.example.domain.dto.request.JobUpdateRequest;
import com.example.domain.dto.response.JobResponse;
import com.example.domain.entity.Company;
import com.example.domain.entity.Job;
import com.example.domain.entity.Skill;
import com.example.domain.entity.User;
import com.example.exception.AppException;
import com.example.filter.FilterParser;
import com.example.filter.JobSpecification;
import com.example.mapper.JobMapper;
import com.example.pagination.PageRequest;
import com.example.pagination.PageResponse;
import com.example.pagination.PagingMeta;
import com.example.repository.CompanyRepository;
import com.example.repository.JobRepository;
import com.example.repository.SkillRepository;
import com.example.repository.UserRepository;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class JobService {

    private static final Logger log = Logger.getLogger(JobService.class);

    @Inject
    JobRepository jobRepository;

    @Inject
    CompanyRepository companyRepository;

    @Inject
    SkillRepository skillRepository;

    @Inject
    UserRepository userRepository;

    @Inject
    JobMapper jobMapper;

    @Inject
    FilterParser filterParser;

    @Transactional
    public JobResponse create(JobCreateRequest request, Long currentUserId) {
        log.info("Creating job: " + request.name);

        // Validate company exists
        Company company = companyRepository.findActiveById(request.companyId)
                .orElseThrow(() -> new AppException(ErrorCode.COMPANY_NOT_FOUND_FOR_JOB.code,
                        ErrorCode.COMPANY_NOT_FOUND_FOR_JOB.message));

        // Validate date range
        if (request.endDate != null && request.startDate != null && request.endDate.isBefore(request.startDate)) {
            throw new AppException(ErrorCode.JOB_INVALID_DATE.code, ErrorCode.JOB_INVALID_DATE.message);
        }

        // Validate skills exist
        List<Skill> skills = new ArrayList<>();
        if (request.skillIds != null && !request.skillIds.isEmpty()) {
            skills = skillRepository.find("id IN ?1 AND deleted = false", request.skillIds).list();
            if (skills.size() != request.skillIds.size()) {
                throw new AppException(ErrorCode.SKILL_NOT_FOUND_FOR_JOB.code, ErrorCode.SKILL_NOT_FOUND_FOR_JOB.message);
            }
        }

        // Create job
        Job job = jobMapper.toEntity(request);
        job.company = company;
        job.skills = skills;
        job.deleted = false;
        job.createdBy = currentUserId;
        job.updatedBy = currentUserId;

        jobRepository.persist(job);
        jobRepository.flush();

        // Reload with skills
        job = jobRepository.findById(job.id);

        log.info("Job created with ID: " + job.id);
        return jobMapper.toDto(job);
    }

    public JobResponse getById(Long id) {
        Job job = jobRepository.findActiveById(id)
                .orElseThrow(() -> new AppException(ErrorCode.JOB_NOT_FOUND.code, ErrorCode.JOB_NOT_FOUND.message));
        return jobMapper.toDto(job);
    }

    public JobResponse getByIdAdmin(Long id) {
        Job job = jobRepository.findById(id);
        if (job == null) {
            throw new AppException(ErrorCode.JOB_NOT_FOUND.code, ErrorCode.JOB_NOT_FOUND.message);
        }
        return jobMapper.toDto(job);
    }

    @Transactional
    public JobResponse update(Long id, JobUpdateRequest request, Long currentUserId, String currentRole, Long currentCompanyId) {
        log.info("Updating job: " + id);

        Job job = jobRepository.findActiveById(id)
                .orElseThrow(() -> new AppException(ErrorCode.JOB_NOT_FOUND.code, ErrorCode.JOB_NOT_FOUND.message));

        // Authorization for RECRUITER
        validateJobOwnership(job, currentUserId, currentRole, currentCompanyId);

        // Cannot edit closed or expired job (RECRUITER only)
        if ("ROLE_RECRUITER".equals(currentRole) && isJobClosed(job)) {
            throw new AppException(ErrorCode.JOB_CLOSED.code, ErrorCode.JOB_CLOSED.message);
        }

        // Validate date range if provided
        Instant startDate = request.startDate != null ? request.startDate : job.startDate;
        Instant endDate = request.endDate != null ? request.endDate : job.endDate;
        if (endDate != null && startDate != null && endDate.isBefore(startDate)) {
            throw new AppException(ErrorCode.JOB_INVALID_DATE.code, ErrorCode.JOB_INVALID_DATE.message);
        }

        // Update skills if provided
        if (request.skillIds != null) {
            List<Skill> skills = skillRepository.find("id IN ?1 AND deleted = false", request.skillIds).list();
            if (skills.size() != request.skillIds.size()) {
                throw new AppException(ErrorCode.SKILL_NOT_FOUND_FOR_JOB.code, ErrorCode.SKILL_NOT_FOUND_FOR_JOB.message);
            }
            job.skills = skills;
        }

        // Update fields
        jobMapper.updateEntity(job, request);
        job.updatedBy = currentUserId;
        jobRepository.persist(job);

        log.info("Job updated: " + id);
        return jobMapper.toDto(job);
    }

    @Transactional
    public void delete(Long id, Long currentUserId, String currentRole, Long currentCompanyId) {
        log.info("Deleting job: " + id);

        Job job = jobRepository.findActiveById(id)
                .orElseThrow(() -> new AppException(ErrorCode.JOB_NOT_FOUND.code, ErrorCode.JOB_NOT_FOUND.message));

        // Authorization
        validateJobOwnership(job, currentUserId, currentRole, currentCompanyId);

        // Soft delete
        job.softDelete();
        job.updatedBy = currentUserId;
        jobRepository.persist(job);

        log.info("Job soft deleted: " + id);
    }

    @Transactional
    public JobResponse close(Long id, Long currentUserId, String currentRole, Long currentCompanyId) {
        log.info("Closing job: " + id);

        Job job = jobRepository.findActiveById(id)
                .orElseThrow(() -> new AppException(ErrorCode.JOB_NOT_FOUND.code, ErrorCode.JOB_NOT_FOUND.message));

        // Authorization
        validateJobOwnership(job, currentUserId, currentRole, currentCompanyId);

        job.close();
        job.updatedBy = currentUserId;
        jobRepository.persist(job);

        log.info("Job closed: " + id);
        return jobMapper.toDto(job);
    }

    @Transactional
    public JobResponse reopen(Long id, Long currentUserId, String currentRole, Long currentCompanyId) {
        log.info("Reopening job: " + id);

        Job job = jobRepository.findById(id);
        if (job == null) {
            throw new AppException(ErrorCode.JOB_NOT_FOUND.code, ErrorCode.JOB_NOT_FOUND.message);
        }

        // Cannot reopen deleted job
        if (job.deleted) {
            throw new AppException(ErrorCode.JOB_CANNOT_REOPEN.code, ErrorCode.JOB_CANNOT_REOPEN.message);
        }

        // Authorization
        validateJobOwnership(job, currentUserId, currentRole, currentCompanyId);

        job.reopen();
        job.updatedBy = currentUserId;
        jobRepository.persist(job);

        log.info("Job reopened: " + id);
        return jobMapper.toDto(job);
    }

    public PageResponse<JobResponse> getAll(PageRequest pageRequest) {
        log.info("Getting jobs - page: " + pageRequest.getPage() + ", size: " + pageRequest.getSize());

        var filter = filterParser.parse(pageRequest.getFilter());
        var queryResult = JobSpecification.buildQuery(filter, pageRequest.getKeyword());
        Sort sort = JobSpecification.buildSort(pageRequest.getSortBy(), pageRequest.isAscending());

        int offset = pageRequest.getOffset();
        int limit = pageRequest.getSize();

        List<Job> jobs = jobRepository.findWithFilter(
                queryResult.query, queryResult.params, sort, offset, limit);
        long total = jobRepository.countWithFilter(queryResult.query, queryResult.params);

        PagingMeta meta = new PagingMeta(
                total,
                (int) Math.ceil((double) total / pageRequest.getSize()),
                pageRequest.getPage(),
                pageRequest.getSize(),
                pageRequest.getSortBy(),
                pageRequest.getDirection()
        );

        List<JobResponse> items = jobMapper.toDtoList(jobs);
        return PageResponse.of(meta, items);
    }

    public PageResponse<JobResponse> search(PageRequest pageRequest, Long skillId, List<Long> skillIds,
                                            Double salaryFrom, Double salaryTo, String location, String level) {
        log.info("Searching jobs with filters");

        List<String> conditions = new ArrayList<>();
        var params = io.quarkus.panache.common.Parameters.with("now", Instant.now());
        conditions.add("(endDate IS NULL OR endDate > :now)");
        conditions.add("deleted = false");

        if (pageRequest.getKeyword() != null && !pageRequest.getKeyword().isBlank()) {
            String kw = "%" + pageRequest.getKeyword() + "%";
            conditions.add("(name LIKE :kw_name OR description LIKE :kw_desc)");
            params.and("kw_name", kw);
            params.and("kw_desc", kw);
        }

        if (skillId != null) {
            String skillQuery = "SELECT j FROM Job j JOIN j.skills s WHERE s.id = :skillId AND (j.endDate IS NULL OR j.endDate > :now) AND j.deleted = false";
            List<Job> jobs = jobRepository.getEntityManager()
                    .createQuery(skillQuery, Job.class)
                    .setParameter("skillId", skillId)
                    .setParameter("now", Instant.now())
                    .getResultList();
            // Manual pagination
            int offset = pageRequest.getOffset();
            int limit = pageRequest.getSize();
            int total = jobs.size();
            jobs = jobs.subList(Math.min(offset, jobs.size()), Math.min(offset + limit, jobs.size()));

            PagingMeta meta = new PagingMeta(
                    total,
                    (int) Math.ceil((double) total / pageRequest.getSize()),
                    pageRequest.getPage(),
                    pageRequest.getSize(),
                    pageRequest.getSortBy(),
                    pageRequest.getDirection()
            );
            return PageResponse.of(meta, jobMapper.toDtoList(jobs));
        }

        if (skillIds != null && !skillIds.isEmpty()) {
            conditions.add("EXISTS (SELECT 1 FROM j.skills s WHERE s.id IN :skillIds)");
            params.and("skillIds", skillIds);
        }

        if (salaryFrom != null) {
            conditions.add("salary >= :salaryFrom");
            params.and("salaryFrom", salaryFrom);
        }

        if (salaryTo != null) {
            conditions.add("salary <= :salaryTo");
            params.and("salaryTo", salaryTo);
        }

        if (location != null && !location.isBlank()) {
            conditions.add("location LIKE :location");
            params.and("location", "%" + location + "%");
        }

        if (level != null && !level.isBlank()) {
            conditions.add("level = :level");
            params.and("level", level);
        }

        String query = String.join(" AND ", conditions);
        Sort sort = JobSpecification.buildSort(pageRequest.getSortBy(), pageRequest.isAscending());

        List<Job> jobs = jobRepository.findWithFilter(query, params, sort,
                pageRequest.getOffset(), pageRequest.getSize());
        long total = jobRepository.countWithFilter(query, params);

        PagingMeta meta = new PagingMeta(
                total,
                (int) Math.ceil((double) total / pageRequest.getSize()),
                pageRequest.getPage(),
                pageRequest.getSize(),
                pageRequest.getSortBy(),
                pageRequest.getDirection()
        );

        return PageResponse.of(meta, jobMapper.toDtoList(jobs));
    }

    public PageResponse<JobResponse> getByCompany(Long companyId, PageRequest pageRequest) {
        log.info("Getting jobs for company: " + companyId);

        String query = "(endDate IS NULL OR endDate > :now) AND deleted = false AND company.id = :companyId";
        var params = io.quarkus.panache.common.Parameters.with("now", Instant.now())
                .and("companyId", companyId);

        Sort sort = JobSpecification.buildSort(pageRequest.getSortBy(), pageRequest.isAscending());

        List<Job> jobs = jobRepository.findWithFilter(query, params, sort,
                pageRequest.getOffset(), pageRequest.getSize());
        long total = jobRepository.countWithFilter(query, params);

        PagingMeta meta = new PagingMeta(
                total,
                (int) Math.ceil((double) total / pageRequest.getSize()),
                pageRequest.getPage(),
                pageRequest.getSize(),
                pageRequest.getSortBy(),
                pageRequest.getDirection()
        );

        return PageResponse.of(meta, jobMapper.toDtoList(jobs));
    }

    private boolean isJobClosed(Job job) {
        return job.endDate != null && job.endDate.isBefore(Instant.now());
    }

    /**
     * Xác thực quyền sở hữu Công việc tuyển dụng cho Nhà tuyển dụng một cách tuyệt đối an toàn.
     * Hỗ trợ 3 tầng kiểm tra: JWT Claim, Tác giả tạo tin (createdBy), và Truy vấn trực tiếp thực tế từ DB.
     */
    private void validateJobOwnership(Job job, Long currentUserId, String currentRole, Long currentCompanyId) {
        if ("ROLE_RECRUITER".equals(currentRole)) {
            // 1. Kiểm tra dựa trên companyId từ JWT token
            if (job.company != null && currentCompanyId != null && job.company.id.equals(currentCompanyId)) {
                return; // Hợp lệ
            }
            
            // 2. Kiểm tra dựa trên tác giả tạo tin (createdBy)
            if (job.createdBy != null && job.createdBy.equals(currentUserId)) {
                return; // Hợp lệ
            }
            
            // 3. Truy vấn trực tiếp DB để lấy thông tin mới nhất (Phòng trường hợp Token bị stale)
            User currentUser = userRepository.findById(currentUserId);
            if (currentUser != null && currentUser.company != null && job.company != null && job.company.id.equals(currentUser.company.id)) {
                return; // Hợp lệ
            }
            
            // Nếu không vượt qua cả 3 chốt chặn bảo mật
            throw new AppException(ErrorCode.JOB_ACCESS_DENIED.code, ErrorCode.JOB_ACCESS_DENIED.message);
        }
    }
}
