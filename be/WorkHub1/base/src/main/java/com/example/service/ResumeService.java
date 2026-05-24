package com.example.service;

import com.example.constant.ErrorCode;
import com.example.constant.StatusEnum;
import com.example.domain.dto.request.ResumeCreateRequest;
import com.example.domain.dto.request.ResumeStatusUpdateRequest;
import com.example.domain.dto.request.ResumeUpdateRequest;
import com.example.domain.dto.response.ResumeResponse;
import com.example.domain.dto.response.JobResponse;
import com.example.domain.dto.response.JobMatchingResponse;
import com.example.domain.dto.response.ResumeMatchingResponse;
import com.example.domain.dto.response.SkillResponse;
import com.example.domain.entity.Job;
import com.example.domain.entity.Resume;
import com.example.domain.entity.Skill;
import com.example.domain.entity.User;
import com.example.exception.AppException;
import com.example.filter.FilterParser;
import com.example.filter.ResumeSpecification;
import com.example.mapper.ResumeMapper;
import com.example.mapper.JobMapper;
import com.example.pagination.PageRequest;
import com.example.pagination.PageResponse;
import com.example.pagination.PagingMeta;
import com.example.repository.JobRepository;
import com.example.repository.ResumeRepository;
import com.example.repository.UserRepository;
import com.example.util.DriveLinkUtil;
import io.quarkus.panache.common.Sort;
import org.jboss.resteasy.reactive.multipart.FileUpload;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
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
    JobMapper jobMapper;

    @Inject
    FilterParser filterParser;

    @Inject
    CvTextExtractorService cvTextExtractorService;

    @Inject
    SkillExtractionService skillExtractionService;

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

    @Transactional
    public ResumeResponse createWithUpload(FileUpload file, String email, Long jobId, Long currentUserId) {
        log.info("Creating resume via file upload for user: " + currentUserId);

        // Validate inputs
        if (file == null) {
            throw new AppException(ErrorCode.RESUME_NOT_FOUND.code, "File upload khong duoc de trong");
        }

        // Validate user exists
        User user = userRepository.findActiveById(currentUserId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND_FOR_RESUME.code,
                        ErrorCode.USER_NOT_FOUND_FOR_RESUME.message));

        // Validate job exists
        Job job = jobRepository.findActiveById(jobId)
                .orElseThrow(() -> new AppException(ErrorCode.JOB_NOT_FOUND.code, ErrorCode.JOB_NOT_FOUND.message));

        // Check duplicate apply
        if (resumeRepository.existsByUserIdAndJobId(currentUserId, jobId)) {
            throw new AppException(ErrorCode.RESUME_ALREADY_EXISTS.code, ErrorCode.RESUME_ALREADY_EXISTS.message);
        }

        try {
            // Save file
            Path uploadDir = Paths.get("uploads");
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }
            String fileName = System.currentTimeMillis() + "_" + file.fileName();
            Path filePath = uploadDir.resolve(fileName);
            Files.copy(file.filePath(), filePath);
            log.info("File saved to: " + filePath.toAbsolutePath());

            // Extract Text & Skills
            String cvText;
            try (InputStream is = Files.newInputStream(filePath)) {
                cvText = cvTextExtractorService.extractText(is, file.fileName());
            }
            List<Skill> matchedSkills = skillExtractionService.extractSkills(cvText);

            // Create resume entity
            Resume resume = new Resume();
            resume.email = email;
            resume.url = "/uploads/" + fileName; // Saved path URL
            resume.user = user;
            resume.job = job;
            resume.status = StatusEnum.PENDING;
            resume.deleted = false;
            resume.createdBy = currentUserId;
            resume.updatedBy = currentUserId;
            resume.skills = matchedSkills;

            resumeRepository.persist(resume);
            resumeRepository.flush();

            log.info("Resume created with ID: " + resume.id + " and " + matchedSkills.size() + " extracted skills");
            return resumeMapper.toDto(resume);
        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error creating resume from file upload", e);
            throw new AppException(500, "Loi khi tai len va phan tich CV: " + e.getMessage());
        }
    }

    @Transactional
    public ResumeResponse createWithDriveLink(String driveUrl, String email, Long jobId, Long currentUserId) {
        log.info("Creating resume via Google Drive link for user: " + currentUserId + ", Link: " + driveUrl);

        // Validate user exists
        User user = userRepository.findActiveById(currentUserId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND_FOR_RESUME.code,
                        ErrorCode.USER_NOT_FOUND_FOR_RESUME.message));

        // Validate job exists
        Job job = jobRepository.findActiveById(jobId)
                .orElseThrow(() -> new AppException(ErrorCode.JOB_NOT_FOUND.code, ErrorCode.JOB_NOT_FOUND.message));

        // Check duplicate apply
        if (resumeRepository.existsByUserIdAndJobId(currentUserId, jobId)) {
            throw new AppException(ErrorCode.RESUME_ALREADY_EXISTS.code, ErrorCode.RESUME_ALREADY_EXISTS.message);
        }

        try {
            // Parse and download Drive Link
            String fileId = DriveLinkUtil.extractFileId(driveUrl);
            String directDownloadUrl = DriveLinkUtil.getDirectDownloadUrl(fileId);
            log.info("Direct download URL: " + directDownloadUrl);

            // Fetch from Google Drive with redirect handling
            java.net.URL url = new java.net.URL(directDownloadUrl);
            java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setInstanceFollowRedirects(true);
            int status = conn.getResponseCode();

            // Handle standard manual redirects if needed
            if (status == java.net.HttpURLConnection.HTTP_MOVED_TEMP
                    || status == java.net.HttpURLConnection.HTTP_MOVED_PERM
                    || status == java.net.HttpURLConnection.HTTP_SEE_OTHER) {
                String newUrl = conn.getHeaderField("Location");
                url = new java.net.URL(newUrl);
                conn = (java.net.HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                status = conn.getResponseCode();
            }

            if (status != java.net.HttpURLConnection.HTTP_OK) {
                throw new AppException(400, "Khong the tai xuong file tu Google Drive. Vui long kiem tra quyen chia se (Anyone with link). HTTP Status: " + status);
            }

            // Determine file name / type
            String contentType = conn.getContentType();
            String disposition = conn.getHeaderField("Content-Disposition");
            String parsedFileName = "cv.pdf"; // Default fallback
            if (disposition != null && disposition.contains("filename=")) {
                int index = disposition.indexOf("filename=");
                parsedFileName = disposition.substring(index + 9).replace("\"", "");
            } else if (contentType != null) {
                if (contentType.contains("application/pdf")) {
                    parsedFileName = "cv.pdf";
                } else if (contentType.contains("word") || contentType.contains("officedocument")) {
                    parsedFileName = "cv.docx";
                }
            }

            log.info("Downloaded file name from Google Drive: " + parsedFileName);

            // Extract Text & Skills
            String cvText;
            try (InputStream is = conn.getInputStream()) {
                cvText = cvTextExtractorService.extractText(is, parsedFileName);
            }
            List<Skill> matchedSkills = skillExtractionService.extractSkills(cvText);

            // Create resume entity
            Resume resume = new Resume();
            resume.email = email;
            resume.url = driveUrl; // Keep the original Drive link
            resume.user = user;
            resume.job = job;
            resume.status = StatusEnum.PENDING;
            resume.deleted = false;
            resume.createdBy = currentUserId;
            resume.updatedBy = currentUserId;
            resume.skills = matchedSkills;

            resumeRepository.persist(resume);
            resumeRepository.flush();

            log.info("Resume created with ID: " + resume.id + " and " + matchedSkills.size() + " extracted skills");
            return resumeMapper.toDto(resume);
        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error creating resume from Google Drive link", e);
            throw new AppException(500, "Loi khi tai len va phan tich CV tu Google Drive: " + e.getMessage());
        }
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

    public List<JobMatchingResponse> getMatchingJobsForResume(Long resumeId, Long currentUserId, String currentRole) {
        log.info("Calculating job matches for resume: " + resumeId);

        Resume resume = resumeRepository.findActiveById(resumeId)
                .orElseThrow(() -> new AppException(ErrorCode.RESUME_NOT_FOUND.code, ErrorCode.RESUME_NOT_FOUND.message));

        // Authorization check: CANDIDATE can only view matches for their own resume
        if ("ROLE_CANDIDATE".equals(currentRole) && !resume.user.id.equals(currentUserId)) {
            throw new AppException(ErrorCode.RESUME_ACCESS_DENIED.code, ErrorCode.RESUME_ACCESS_DENIED.message);
        }

        // Fetch active open jobs
        List<Job> openJobs = Job.list("deleted = false AND (endDate IS NULL OR endDate > ?1)", java.time.Instant.now());
        List<JobMatchingResponse> matchingJobs = new ArrayList<>();

        List<Skill> resumeSkills = resume.skills != null ? resume.skills : new ArrayList<>();

        for (Job job : openJobs) {
            List<Skill> jobSkills = job.skills != null ? job.skills : new ArrayList<>();

            if (jobSkills.isEmpty()) {
                JobResponse jobDto = jobMapper.toDto(job);
                matchingJobs.add(new JobMatchingResponse(jobDto, 100.0, new ArrayList<>(), new ArrayList<>()));
                continue;
            }

            List<SkillResponse> matchedSkills = new ArrayList<>();
            List<SkillResponse> missingSkills = new ArrayList<>();

            for (Skill js : jobSkills) {
                boolean hasSkill = resumeSkills.stream().anyMatch(rs -> rs.id.equals(js.id));
                SkillResponse jsDto = new SkillResponse(js.id, js.name, js.description, js.level, js.deleted, js.createdAt, js.updatedAt, js.createdBy, js.updatedBy);
                if (hasSkill) {
                    matchedSkills.add(jsDto);
                } else {
                    missingSkills.add(jsDto);
                }
            }

            double score = ((double) matchedSkills.size() / jobSkills.size()) * 100.0;
            score = Math.round(score * 10.0) / 10.0;

            JobResponse jobDto = jobMapper.toDto(job);
            matchingJobs.add(new JobMatchingResponse(jobDto, score, matchedSkills, missingSkills));
        }

        // Sort descending by match score
        matchingJobs.sort((a, b) -> Double.compare(b.matchScore, a.matchScore));

        log.info("Matching complete. Found " + matchingJobs.size() + " matches.");
        return matchingJobs;
    }

    public List<ResumeMatchingResponse> getMatchingCandidatesForJob(Long jobId, Long currentUserId, String currentRole) {
        log.info("Calculating candidate matching scores for job: " + jobId);

        Job job = jobRepository.findActiveById(jobId)
                .orElseThrow(() -> new AppException(ErrorCode.JOB_NOT_FOUND.code, ErrorCode.JOB_NOT_FOUND.message));

        List<Resume> resumes = Resume.list("job.id = ?1 AND deleted = false", jobId);
        List<ResumeMatchingResponse> matchedResumes = new ArrayList<>();

        List<Skill> jobSkills = job.skills != null ? job.skills : new ArrayList<>();

        for (Resume resume : resumes) {
            List<Skill> resumeSkills = resume.skills != null ? resume.skills : new ArrayList<>();

            if (jobSkills.isEmpty()) {
                ResumeResponse resumeDto = resumeMapper.toDto(resume);
                matchedResumes.add(new ResumeMatchingResponse(resumeDto, 100.0, new ArrayList<>(), new ArrayList<>()));
                continue;
            }

            List<SkillResponse> matchedSkills = new ArrayList<>();
            List<SkillResponse> missingSkills = new ArrayList<>();

            for (Skill js : jobSkills) {
                boolean hasSkill = resumeSkills.stream().anyMatch(rs -> rs.id.equals(js.id));
                SkillResponse jsDto = new SkillResponse(js.id, js.name, js.description, js.level, js.deleted, js.createdAt, js.updatedAt, js.createdBy, js.updatedBy);
                if (hasSkill) {
                    matchedSkills.add(jsDto);
                } else {
                    missingSkills.add(jsDto);
                }
            }

            double score = ((double) matchedSkills.size() / jobSkills.size()) * 100.0;
            score = Math.round(score * 10.0) / 10.0;

            ResumeResponse resumeDto = resumeMapper.toDto(resume);
            matchedResumes.add(new ResumeMatchingResponse(resumeDto, score, matchedSkills, missingSkills));
        }

        // Sort descending by match score
        matchedResumes.sort((a, b) -> Double.compare(b.matchScore, a.matchScore));

        log.info("Matched " + matchedResumes.size() + " candidates.");
        return matchedResumes;
    }
}