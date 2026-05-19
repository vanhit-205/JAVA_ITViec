package com.example.resource;

import com.example.base.BaseResponse;
import com.example.domain.dto.request.JobCreateRequest;
import com.example.domain.dto.request.JobUpdateRequest;
import com.example.domain.dto.response.JobResponse;
import com.example.pagination.PageRequest;
import com.example.pagination.PageResponse;
import com.example.security.SecurityContext;
import com.example.service.JobService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;

import java.util.List;

@Path("/api/v1/jobs")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class JobResource {

    private static final Logger log = Logger.getLogger(JobResource.class);

    @Inject
    JobService jobService;

    @Inject
    SecurityContext securityContext;

    @POST
    @RolesAllowed({"ROLE_ADMIN", "ROLE_RECRUITER"})
    public Response create(@Valid JobCreateRequest request) {
        log.info("Create job request");
        Long currentUserId = securityContext.getCurrentUserId();
        String currentRole = securityContext.getCurrentUserRole();
        Long currentCompanyId = securityContext.getCurrentCompanyId();

        // RECRUITER can only create job for their own company
        if ("ROLE_RECRUITER".equals(currentRole) && currentCompanyId != null && !currentCompanyId.equals(request.companyId)) {
            throw new com.example.exception.AppException(403, "You can only create jobs for your company");
        }

        JobResponse job = jobService.create(request, currentUserId);
        return Response.status(Response.Status.CREATED)
                .entity(BaseResponse.success(job, 201, "/api/v1/jobs"))
                .build();
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({"ROLE_ADMIN", "ROLE_RECRUITER", "ROLE_CANDIDATE"})
    public Response getById(@PathParam("id") Long id) {
        log.info("Get job by ID: " + id);
        JobResponse job = jobService.getById(id);
        return Response.ok(BaseResponse.success(job, 200, "/api/v1/jobs/" + id))
                .build();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed({"ROLE_ADMIN", "ROLE_RECRUITER"})
    public Response update(@PathParam("id") Long id, @Valid JobUpdateRequest request) {
        log.info("Update job: " + id);
        Long currentUserId = securityContext.getCurrentUserId();
        String currentRole = securityContext.getCurrentUserRole();
        Long currentCompanyId = securityContext.getCurrentCompanyId();

        JobResponse job = jobService.update(id, request, currentUserId, currentRole, currentCompanyId);
        return Response.ok(BaseResponse.success(job, 200, "/api/v1/jobs/" + id))
                .build();
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed({"ROLE_ADMIN", "ROLE_RECRUITER"})
    public Response delete(@PathParam("id") Long id) {
        log.info("Delete job: " + id);
        Long currentUserId = securityContext.getCurrentUserId();
        String currentRole = securityContext.getCurrentUserRole();
        Long currentCompanyId = securityContext.getCurrentCompanyId();

        jobService.delete(id, currentUserId, currentRole, currentCompanyId);
        return Response.ok(BaseResponse.success("Job deleted successfully", 200, "/api/v1/jobs/" + id))
                .build();
    }

    @PATCH
    @Path("/{id}/close")
    @RolesAllowed({"ROLE_ADMIN", "ROLE_RECRUITER"})
    public Response close(@PathParam("id") Long id) {
        log.info("Close job: " + id);
        Long currentUserId = securityContext.getCurrentUserId();
        String currentRole = securityContext.getCurrentUserRole();
        Long currentCompanyId = securityContext.getCurrentCompanyId();

        JobResponse job = jobService.close(id, currentUserId, currentRole, currentCompanyId);
        return Response.ok(BaseResponse.success(job, 200, "/api/v1/jobs/" + id + "/close"))
                .build();
    }

    @PATCH
    @Path("/{id}/reopen")
    @RolesAllowed({"ROLE_ADMIN", "ROLE_RECRUITER"})
    public Response reopen(@PathParam("id") Long id) {
        log.info("Reopen job: " + id);
        Long currentUserId = securityContext.getCurrentUserId();
        String currentRole = securityContext.getCurrentUserRole();
        Long currentCompanyId = securityContext.getCurrentCompanyId();

        JobResponse job = jobService.reopen(id, currentUserId, currentRole, currentCompanyId);
        return Response.ok(BaseResponse.success(job, 200, "/api/v1/jobs/" + id + "/reopen"))
                .build();
    }

    @GET
    @RolesAllowed({"ROLE_ADMIN", "ROLE_RECRUITER", "ROLE_CANDIDATE"})
    public Response getAll(
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("size") @DefaultValue("10") int size,
            @QueryParam("sortBy") @DefaultValue("createdAt") String sortBy,
            @QueryParam("direction") @DefaultValue("DESC") String direction,
            @QueryParam("keyword") String keyword,
            @QueryParam("filter") String filter) {

        log.info("Get all jobs - page: " + page + ", size: " + size);

        PageRequest pageRequest = new PageRequest();
        pageRequest.setPage(page);
        pageRequest.setSize(size);
        pageRequest.setSortBy(sortBy);
        pageRequest.setDirection(direction);
        pageRequest.setKeyword(keyword);
        pageRequest.setFilter(filter);

        PageResponse<JobResponse> response = jobService.getAll(pageRequest);
        return Response.ok(BaseResponse.success(response, 200, "/api/v1/jobs"))
                .build();
    }

    @GET
    @Path("/search")
    @RolesAllowed({"ROLE_ADMIN", "ROLE_RECRUITER", "ROLE_CANDIDATE"})
    public Response search(
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("size") @DefaultValue("10") int size,
            @QueryParam("sortBy") @DefaultValue("createdAt") String sortBy,
            @QueryParam("direction") @DefaultValue("DESC") String direction,
            @QueryParam("keyword") String keyword,
            @QueryParam("skill") Long skillId,
            @QueryParam("skills") String skillsParam,
            @QueryParam("salaryFrom") Double salaryFrom,
            @QueryParam("salaryTo") Double salaryTo,
            @QueryParam("location") String location,
            @QueryParam("level") String level,
            @QueryParam("filter") String filter) {

        log.info("Search jobs");

        // Parse skills list from comma-separated string
        List<Long> skillIds = null;
        if (skillsParam != null && !skillsParam.isBlank()) {
            skillIds = new java.util.ArrayList<>();
            for (String s : skillsParam.split(",")) {
                try {
                    skillIds.add(Long.parseLong(s.trim()));
                } catch (NumberFormatException ignored) {}
            }
        }

        PageRequest pageRequest = new PageRequest();
        pageRequest.setPage(page);
        pageRequest.setSize(size);
        pageRequest.setSortBy(sortBy);
        pageRequest.setDirection(direction);
        pageRequest.setKeyword(keyword);
        pageRequest.setFilter(filter);

        PageResponse<JobResponse> response = jobService.search(pageRequest, skillId, skillIds,
                salaryFrom, salaryTo, location, level);
        return Response.ok(BaseResponse.success(response, 200, "/api/v1/jobs/search"))
                .build();
    }

    @GET
    @Path("/skills/{skillId}")
    @RolesAllowed({"ROLE_ADMIN", "ROLE_RECRUITER", "ROLE_CANDIDATE"})
    public Response getBySkill(
            @PathParam("skillId") Long skillId,
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("size") @DefaultValue("10") int size,
            @QueryParam("sortBy") @DefaultValue("createdAt") String sortBy,
            @QueryParam("direction") @DefaultValue("DESC") String direction) {

        log.info("Get jobs by skill: " + skillId);

        PageRequest pageRequest = new PageRequest();
        pageRequest.setPage(page);
        pageRequest.setSize(size);
        pageRequest.setSortBy(sortBy);
        pageRequest.setDirection(direction);

        PageResponse<JobResponse> response = jobService.search(pageRequest, skillId, null, null, null, null, null);
        return Response.ok(BaseResponse.success(response, 200, "/api/v1/jobs/skills/" + skillId))
                .build();
    }

    @GET
    @Path("/company/{companyId}")
    @RolesAllowed({"ROLE_ADMIN", "ROLE_RECRUITER", "ROLE_CANDIDATE"})
    public Response getByCompany(
            @PathParam("companyId") Long companyId,
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("size") @DefaultValue("10") int size,
            @QueryParam("sortBy") @DefaultValue("createdAt") String sortBy,
            @QueryParam("direction") @DefaultValue("DESC") String direction) {

        log.info("Get jobs for company: " + companyId);

        PageRequest pageRequest = new PageRequest();
        pageRequest.setPage(page);
        pageRequest.setSize(size);
        pageRequest.setSortBy(sortBy);
        pageRequest.setDirection(direction);

        PageResponse<JobResponse> response = jobService.getByCompany(companyId, pageRequest);
        return Response.ok(BaseResponse.success(response, 200, "/api/v1/jobs/company/" + companyId))
                .build();
    }

    @GET
    @Path("/{jobId}/resumes")
    @RolesAllowed({"ROLE_ADMIN", "ROLE_RECRUITER"})
    public Response getJobResumes(
            @PathParam("jobId") Long jobId,
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("size") @DefaultValue("10") int size,
            @QueryParam("sortBy") @DefaultValue("createdAt") String sortBy,
            @QueryParam("direction") @DefaultValue("DESC") String direction,
            @QueryParam("filter") String filter) {

        log.info("Get resumes for job: " + jobId);

        // TODO: implement get resumes by job
        // For now return empty response
        return Response.ok(BaseResponse.success(null, 200, "/api/v1/jobs/" + jobId + "/resumes"))
                .build();
    }
}
