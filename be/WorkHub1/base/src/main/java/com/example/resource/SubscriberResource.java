package com.example.resource;

import com.example.base.BaseResponse;
import com.example.domain.dto.request.SubscriberCreateRequest;
import com.example.domain.dto.request.SubscriberUpdateRequest;
import com.example.domain.dto.response.SubscriberResponse;
import com.example.pagination.PageRequest;
import com.example.pagination.PageResponse;
import com.example.security.SecurityContext;
import com.example.service.SubscriberService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;

@Path("/api/v1/subscribers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SubscriberResource {

    private static final Logger log = Logger.getLogger(SubscriberResource.class);

    @Inject
    SubscriberService subscriberService;

    @Inject
    SecurityContext securityContext;

    @POST
    @RolesAllowed({"ROLE_ADMIN", "ROLE_CANDIDATE"})
    public Response create(@Valid SubscriberCreateRequest request) {
        log.info("Create subscriber request");
        Long currentUserId = securityContext.getCurrentUserId();
        SubscriberResponse subscriber = subscriberService.create(request, currentUserId);
        return Response.status(Response.Status.CREATED)
                .entity(BaseResponse.success(subscriber, 201, "/api/v1/subscribers"))
                .build();
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({"ROLE_ADMIN", "ROLE_CANDIDATE"})
    public Response getById(@PathParam("id") Long id) {
        log.info("Get subscriber by ID: " + id);
        SubscriberResponse subscriber = subscriberService.getById(id);
        return Response.ok(BaseResponse.success(subscriber, 200, "/api/v1/subscribers/" + id))
                .build();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed({"ROLE_ADMIN", "ROLE_CANDIDATE"})
    public Response update(@PathParam("id") Long id, @Valid SubscriberUpdateRequest request) {
        log.info("Update subscriber: " + id);
        Long currentUserId = securityContext.getCurrentUserId();
        SubscriberResponse subscriber = subscriberService.update(id, request, currentUserId);
        return Response.ok(BaseResponse.success(subscriber, 200, "/api/v1/subscribers/" + id))
                .build();
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed({"ROLE_ADMIN", "ROLE_CANDIDATE"})
    public Response delete(@PathParam("id") Long id) {
        log.info("Delete subscriber: " + id);
        Long currentUserId = securityContext.getCurrentUserId();
        subscriberService.delete(id, currentUserId);
        return Response.ok(BaseResponse.success("Subscriber deleted successfully", 200, "/api/v1/subscribers/" + id))
                .build();
    }

    @PATCH
    @Path("/{id}/enable")
    @RolesAllowed("ROLE_ADMIN")
    public Response enable(@PathParam("id") Long id) {
        log.info("Enable subscriber: " + id);
        Long currentUserId = securityContext.getCurrentUserId();
        SubscriberResponse subscriber = subscriberService.enable(id, currentUserId);
        return Response.ok(BaseResponse.success(subscriber, 200, "/api/v1/subscribers/" + id + "/enable"))
                .build();
    }

    @PATCH
    @Path("/{id}/disable")
    @RolesAllowed("ROLE_ADMIN")
    public Response disable(@PathParam("id") Long id) {
        log.info("Disable subscriber: " + id);
        Long currentUserId = securityContext.getCurrentUserId();
        SubscriberResponse subscriber = subscriberService.disable(id, currentUserId);
        return Response.ok(BaseResponse.success(subscriber, 200, "/api/v1/subscribers/" + id + "/disable"))
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

        log.info("Get all subscribers - page: " + page + ", size: " + size);

        PageRequest pageRequest = new PageRequest();
        pageRequest.setPage(page);
        pageRequest.setSize(size);
        pageRequest.setSortBy(sortBy);
        pageRequest.setDirection(direction);
        pageRequest.setKeyword(keyword);
        pageRequest.setFilter(filter);

        PageResponse<SubscriberResponse> response = subscriberService.getAll(pageRequest);
        return Response.ok(BaseResponse.success(response, 200, "/api/v1/subscribers"))
                .build();
    }
}