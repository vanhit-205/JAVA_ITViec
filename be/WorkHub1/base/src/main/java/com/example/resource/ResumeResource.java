package com.example.resource;

import com.example.base.BaseResponse;
import com.example.domain.dto.request.ResumeCreateRequest;
import com.example.domain.dto.request.ResumeStatusUpdateRequest;
import com.example.domain.dto.request.ResumeUpdateRequest;
import com.example.domain.dto.response.ResumeResponse;
import com.example.pagination.PageRequest;
import com.example.pagination.PageResponse;
import com.example.security.SecurityContext;
import com.example.service.ResumeService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;

@Path("/api/v1/resumes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ResumeResource {

    private static final Logger log = Logger.getLogger(ResumeResource.class);

    @Inject
    ResumeService resumeService;

    @Inject
    SecurityContext securityContext;

    @POST
    @RolesAllowed({"ROLE_CANDIDATE", "ROLE_ADMIN"})
    public Response create(@Valid ResumeCreateRequest request) {
        log.info("Create resume request");
        Long currentUserId = securityContext.getCurrentUserId();
        ResumeResponse resume = resumeService.create(request, currentUserId);
        return Response.status(Response.Status.CREATED)
                .entity(BaseResponse.success(resume, 201, "/api/v1/resumes"))
                .build();
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({"ROLE_ADMIN", "ROLE_RECRUITER", "ROLE_CANDIDATE"})
    public Response getById(@PathParam("id") Long id) {
        log.info("Get resume by ID: " + id);
        Long currentUserId = securityContext.getCurrentUserId();
        String currentRole = securityContext.getCurrentUserRole();

        ResumeResponse resume;
        if ("ROLE_ADMIN".equals(currentRole)) {
            resume = resumeService.getByIdAdmin(id);
        } else {
            resume = resumeService.getById(id, currentUserId, currentRole);
        }
        return Response.ok(BaseResponse.success(resume, 200, "/api/v1/resumes/" + id))
                .build();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed({"ROLE_ADMIN", "ROLE_CANDIDATE"})
    public Response update(@PathParam("id") Long id, @Valid ResumeUpdateRequest request) {
        log.info("Update resume: " + id);
        Long currentUserId = securityContext.getCurrentUserId();
        String currentRole = securityContext.getCurrentUserRole();

        ResumeResponse resume = resumeService.update(id, request, currentUserId, currentRole);
        return Response.ok(BaseResponse.success(resume, 200, "/api/v1/resumes/" + id))
                .build();
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed({"ROLE_ADMIN", "ROLE_CANDIDATE", "ROLE_RECRUITER"})
    public Response delete(@PathParam("id") Long id) {
        log.info("Delete resume: " + id);
        Long currentUserId = securityContext.getCurrentUserId();
        String currentRole = securityContext.getCurrentUserRole();

        resumeService.delete(id, currentUserId, currentRole);
        return Response.ok(BaseResponse.success("Resume deleted successfully", 200, "/api/v1/resumes/" + id))
                .build();
    }

    @PATCH
    @Path("/{id}/status")
    @RolesAllowed({"ROLE_ADMIN", "ROLE_RECRUITER"})
    public Response updateStatus(@PathParam("id") Long id, @Valid ResumeStatusUpdateRequest request) {
        log.info("Update resume status: " + id);
        Long currentUserId = securityContext.getCurrentUserId();
        String currentRole = securityContext.getCurrentUserRole();

        ResumeResponse resume = resumeService.updateStatus(id, request, currentUserId, currentRole);
        return Response.ok(BaseResponse.success(resume, 200, "/api/v1/resumes/" + id + "/status"))
                .build();
    }

    @GET
    @RolesAllowed({"ROLE_ADMIN", "ROLE_RECRUITER"})
    public Response getAll(
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("size") @DefaultValue("10") int size,
            @QueryParam("sortBy") @DefaultValue("createdAt") String sortBy,
            @QueryParam("direction") @DefaultValue("DESC") String direction,
            @QueryParam("keyword") String keyword,
            @QueryParam("filter") String filter) {

        log.info("Get all resumes - page: " + page + ", size: " + size);

        PageRequest pageRequest = new PageRequest();
        pageRequest.setPage(page);
        pageRequest.setSize(size);
        pageRequest.setSortBy(sortBy);
        pageRequest.setDirection(direction);
        pageRequest.setKeyword(keyword);
        pageRequest.setFilter(filter);

        PageResponse<ResumeResponse> response = resumeService.getAll(pageRequest);
        return Response.ok(BaseResponse.success(response, 200, "/api/v1/resumes"))
                .build();
    }

    @GET
    @Path("/my")
    @RolesAllowed("ROLE_CANDIDATE")
    public Response getMyResumes(
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("size") @DefaultValue("10") int size,
            @QueryParam("sortBy") @DefaultValue("createdAt") String sortBy,
            @QueryParam("direction") @DefaultValue("DESC") String direction,
            @QueryParam("keyword") String keyword,
            @QueryParam("filter") String filter) {

        log.info("Get my resumes - page: " + page + ", size: " + size);
        Long currentUserId = securityContext.getCurrentUserId();

        PageRequest pageRequest = new PageRequest();
        pageRequest.setPage(page);
        pageRequest.setSize(size);
        pageRequest.setSortBy(sortBy);
        pageRequest.setDirection(direction);
        pageRequest.setKeyword(keyword);
        pageRequest.setFilter(filter);

        PageResponse<ResumeResponse> response = resumeService.getByUser(currentUserId, pageRequest);
        return Response.ok(BaseResponse.success(response, 200, "/api/v1/resumes/my"))
                .build();
    }

    @GET
    @Path("/company/{companyId}")
    @RolesAllowed({"ROLE_ADMIN", "ROLE_RECRUITER"})
    public Response getByCompany(
            @PathParam("companyId") Long companyId,
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("size") @DefaultValue("10") int size,
            @QueryParam("sortBy") @DefaultValue("createdAt") String sortBy,
            @QueryParam("direction") @DefaultValue("DESC") String direction,
            @QueryParam("keyword") String keyword,
            @QueryParam("filter") String filter) {

        log.info("Get resumes for company: " + companyId);
        String currentRole = securityContext.getCurrentUserRole();

        // RECRUITER can only view resumes for their own company
        if ("ROLE_RECRUITER".equals(currentRole)) {
            Long currentUserId = securityContext.getCurrentUserId();
            // TODO: Validate company ownership
        }

        PageRequest pageRequest = new PageRequest();
        pageRequest.setPage(page);
        pageRequest.setSize(size);
        pageRequest.setSortBy(sortBy);
        pageRequest.setDirection(direction);
        pageRequest.setKeyword(keyword);
        pageRequest.setFilter(filter);

        PageResponse<ResumeResponse> response = resumeService.getByCompany(companyId, pageRequest);
        return Response.ok(BaseResponse.success(response, 200, "/api/v1/resumes/company/" + companyId))
                .build();
    }
}
