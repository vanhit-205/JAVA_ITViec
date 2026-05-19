package com.example.resource;

import com.example.base.BaseResponse;
import com.example.domain.entity.RoleUpgradeRequest;
import com.example.security.SecurityContext;
import com.example.service.RoleUpgradeService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/v1/role-upgrades")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoleUpgradeResource {

    @Inject
    RoleUpgradeService roleUpgradeService;

    @Inject
    SecurityContext securityContext;

    public static class UpgradeRequestPayload {
        public Long companyId;
        public String newCompanyName;
        public String reason;
    }

    public static class RejectPayload {
        public String adminNotes;
    }

    @POST
    @Path("/request")
    @RolesAllowed("ROLE_CANDIDATE")
    public Response requestUpgrade(UpgradeRequestPayload payload) {
        Long userId = securityContext.getCurrentUserId();
        RoleUpgradeRequest result = roleUpgradeService.createRequest(
                userId, 
                payload.companyId, 
                payload.newCompanyName, 
                payload.reason
        );
        return Response.status(Response.Status.CREATED)
                .entity(BaseResponse.success(result, 201, "/api/v1/role-upgrades/request"))
                .build();
    }

    @GET
    @Path("/my-request")
    @RolesAllowed("ROLE_CANDIDATE")
    public Response getMyRequest() {
        Long userId = securityContext.getCurrentUserId();
        RoleUpgradeRequest result = roleUpgradeService.getMyRequest(userId);
        return Response.ok(BaseResponse.success(result, 200, "/api/v1/role-upgrades/my-request"))
                .build();
    }

    @GET
    @Path("/list")
    @RolesAllowed("ROLE_ADMIN")
    public Response getAllRequests() {
        return Response.ok(BaseResponse.success(roleUpgradeService.getAllRequests(), 200, "/api/v1/role-upgrades/list"))
                .build();
    }

    @PUT
    @Path("/{id}/approve")
    @RolesAllowed("ROLE_ADMIN")
    public Response approveRequest(@PathParam("id") Long id) {
        Long adminId = securityContext.getCurrentUserId();
        roleUpgradeService.approveRequest(id, adminId);
        return Response.ok(BaseResponse.success("Phê duyệt nâng cấp vai trò thành công!", 200, "/api/v1/role-upgrades/" + id + "/approve"))
                .build();
    }

    @PUT
    @Path("/{id}/reject")
    @RolesAllowed("ROLE_ADMIN")
    public Response rejectRequest(@PathParam("id") Long id, RejectPayload payload) {
        Long adminId = securityContext.getCurrentUserId();
        roleUpgradeService.rejectRequest(id, adminId, payload.adminNotes);
        return Response.ok(BaseResponse.success("Đã từ chối yêu cầu nâng cấp vai trò.", 200, "/api/v1/role-upgrades/" + id + "/reject"))
                .build();
    }
}
