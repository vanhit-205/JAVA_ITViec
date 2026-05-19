package com.example.resource;

import com.example.base.BaseResponse;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;

/**
 * Sample Admin API - only ROLE_ADMIN can access
 */
@Path("/api/v1/admin")
@Produces(MediaType.APPLICATION_JSON)
public class AdminResource {

    private static final Logger log = Logger.getLogger(AdminResource.class);

    @GET
    @Path("/dashboard")
    @RolesAllowed("ROLE_ADMIN")
    public Response getDashboard() {
        log.info("Admin dashboard accessed");
        return Response.ok(BaseResponse.success("Welcome to Admin Dashboard", 200, "/api/v1/admin/dashboard"))
                .build();
    }

    @GET
    @Path("/users")
    @RolesAllowed("ROLE_ADMIN")
    public Response getAllUsers() {
        log.info("Admin getting all users");
        return Response.ok(BaseResponse.success("List of all users", 200, "/api/v1/admin/users"))
                .build();
    }
}
