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
 * Sample Recruiter API - only ROLE_RECRUITER can access
 */
@Path("/api/v1/recruiter")
@Produces(MediaType.APPLICATION_JSON)
public class RecruiterResource {

    private static final Logger log = Logger.getLogger(RecruiterResource.class);

    @GET
    @Path("/jobs")
    @RolesAllowed("ROLE_RECRUITER")
    public Response getMyJobs() {
        log.info("Recruiter accessing jobs");
        return Response.ok(BaseResponse.success("List of recruiter's jobs", 200, "/api/v1/recruiter/jobs"))
                .build();
    }

    @GET
    @Path("/candidates")
    @RolesAllowed("ROLE_RECRUITER")
    public Response getCandidates() {
        log.info("Recruiter accessing candidates");
        return Response.ok(BaseResponse.success("List of candidates", 200, "/api/v1/recruiter/candidates"))
                .build();
    }
}
