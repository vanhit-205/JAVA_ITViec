package com.example.resource;

import com.example.base.BaseResponse;
import com.example.domain.dto.request.UserCreateRequest;
import com.example.domain.dto.request.UserUpdateRequest;
import com.example.domain.dto.response.UserResponse;
import com.example.pagination.PageRequest;
import com.example.pagination.PageResponse;
import com.example.security.SecurityContext;
import com.example.service.UserService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;

@Path("/api/v1/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

    private static final Logger log = Logger.getLogger(UserResource.class);

    @Inject
    UserService userService;

    @Inject
    SecurityContext securityContext;

    @POST
    @RolesAllowed("ROLE_ADMIN")
    public Response create(@Valid UserCreateRequest request) {
        log.info("Create user request");
        Long currentUserId = securityContext.getCurrentUserId();
        UserResponse user = userService.create(request, currentUserId);
        return Response.status(Response.Status.CREATED)
                .entity(BaseResponse.success(user, 201, "/api/v1/users"))
                .build();
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({"ROLE_ADMIN", "ROLE_RECRUITER", "ROLE_CANDIDATE"})
    public Response getById(@PathParam("id") Long id) {
        log.info("Get user by ID: " + id);
        UserResponse user = userService.getById(id);
        return Response.ok(BaseResponse.success(user, 200, "/api/v1/users/" + id))
                .build();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed({"ROLE_ADMIN", "ROLE_CANDIDATE", "ROLE_RECRUITER"})
    public Response update(@PathParam("id") Long id, @Valid UserUpdateRequest request) {
        log.info("Update user: " + id);

        // CANDIDATE and RECRUITER can only update their own profile
        Long currentUserId = securityContext.getCurrentUserId();
        String currentRole = securityContext.getCurrentUserRole();

        if (!"ROLE_ADMIN".equals(currentRole) && !id.equals(currentUserId)) {
            throw new com.example.exception.AppException(403, "You can only update your own profile");
        }

        UserResponse user = userService.update(id, request, currentUserId);
        return Response.ok(BaseResponse.success(user, 200, "/api/v1/users/" + id))
                .build();
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed("ROLE_ADMIN")
    public Response delete(@PathParam("id") Long id) {
        log.info("Delete user: " + id);
        Long currentUserId = securityContext.getCurrentUserId();
        userService.delete(id, currentUserId);
        return Response.ok(BaseResponse.success("User deleted successfully", 200, "/api/v1/users/" + id))
                .build();
    }

    @PATCH
    @Path("/{id}/lock")
    @RolesAllowed("ROLE_ADMIN")
    public Response lock(@PathParam("id") Long id) {
        log.info("Lock user: " + id);
        Long currentUserId = securityContext.getCurrentUserId();
        userService.lock(id, currentUserId);
        return Response.ok(BaseResponse.success("User locked successfully", 200, "/api/v1/users/" + id + "/lock"))
                .build();
    }

    @PATCH
    @Path("/{id}/unlock")
    @RolesAllowed("ROLE_ADMIN")
    public Response unlock(@PathParam("id") Long id) {
        log.info("Unlock user: " + id);
        Long currentUserId = securityContext.getCurrentUserId();
        userService.unlock(id, currentUserId);
        return Response.ok(BaseResponse.success("User unlocked successfully", 200, "/api/v1/users/" + id + "/unlock"))
                .build();
    }

    @PATCH
    @Path("/{id}/disable")
    @RolesAllowed("ROLE_ADMIN")
    public Response disable(@PathParam("id") Long id) {
        log.info("Disable user: " + id);
        Long currentUserId = securityContext.getCurrentUserId();
        userService.disable(id, currentUserId);
        return Response.ok(BaseResponse.success("User disabled successfully", 200, "/api/v1/users/" + id + "/disable"))
                .build();
    }

    @PATCH
    @Path("/{id}/enable")
    @RolesAllowed("ROLE_ADMIN")
    public Response enable(@PathParam("id") Long id) {
        log.info("Enable user: " + id);
        Long currentUserId = securityContext.getCurrentUserId();
        userService.enable(id, currentUserId);
        return Response.ok(BaseResponse.success("User enabled successfully", 200, "/api/v1/users/" + id + "/enable"))
                .build();
    }

    @GET
    @RolesAllowed("ROLE_ADMIN")
    public Response getAll(
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("size") @DefaultValue("10") int size,
            @QueryParam("sortBy") @DefaultValue("createdAt") String sortBy,
            @QueryParam("direction") @DefaultValue("DESC") String direction,
            @QueryParam("keyword") String keyword,
            @QueryParam("filter") String filter) {

        log.info("Get all users - page: " + page + ", size: " + size);

        PageRequest pageRequest = new PageRequest();
        pageRequest.setPage(page);
        pageRequest.setSize(size);
        pageRequest.setSortBy(sortBy);
        pageRequest.setDirection(direction);
        pageRequest.setKeyword(keyword);
        pageRequest.setFilter(filter);

        PageResponse<UserResponse> response = userService.getAll(pageRequest);
        return Response.ok(BaseResponse.success(response, 200, "/api/v1/users"))
                .build();
    }
}
