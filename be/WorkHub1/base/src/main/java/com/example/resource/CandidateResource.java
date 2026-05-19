package com.example.resource;

import com.example.base.BaseResponse;
import com.example.security.SecurityContext;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;

/**
 * Sample Candidate API - only ROLE_CANDIDATE can access
 */
@Path("/api/v1/candidate")
@Produces(MediaType.APPLICATION_JSON)
public class CandidateResource {

    private static final Logger log = Logger.getLogger(CandidateResource.class);

    @Inject
    SecurityContext securityContext;

    @GET
    @Path("/profile")
    @RolesAllowed("ROLE_CANDIDATE")
    public Response getMyProfile() {
        log.info("Candidate accessing profile: " + securityContext.getCurrentUserEmail());
        return Response.ok(BaseResponse.success("Candidate profile data", 200, "/api/v1/candidate/profile"))
                .build();
    }

    @GET
    @Path("/jobs")
    @RolesAllowed("ROLE_CANDIDATE")
    public Response getAvailableJobs() {
        log.info("Candidate accessing available jobs");
        return Response.ok(BaseResponse.success("List of available jobs", 200, "/api/v1/candidate/jobs"))
                .build();
    }
}
