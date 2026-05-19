package com.example.resource;

import com.example.base.BaseResponse;
import com.example.service.EmailServiceImpl;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;

@Path("/api/v1/test")
@Produces(MediaType.APPLICATION_JSON)
public class EmailTestResource {

    private static final Logger log = Logger.getLogger(EmailTestResource.class);

    @Inject
    EmailServiceImpl emailService;

    /**
     * Manually trigger job matching email job
     * Only ADMIN can access
     */
    @POST
    @Path("/trigger-job-matching-email")
    @RolesAllowed("ROLE_ADMIN")
    public Response triggerJobMatchingEmail() {
        log.info("Manually triggering job matching email job");
        try {
            emailService.sendJobMatchingEmails();
            return Response.ok(BaseResponse.success("Job matching emails sent successfully", 200, "/api/v1/test/trigger-job-matching-email"))
                    .build();
        } catch (Exception e) {
            log.error("Error triggering job matching email job", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(BaseResponse.success("Error: " + e.getMessage(), 500, "/api/v1/test/trigger-job-matching-email"))
                    .build();
        }
    }

    /**
     * Test find matching jobs only (without sending email)
     */
    @POST
    @Path("/test-matching-jobs")
    @RolesAllowed("ROLE_ADMIN")
    public Response testMatchingJobs() {
        log.info("Testing job matching logic");
        try {
            // Get first enabled subscriber
            var subscribers = emailService.findMatchingJobsForTest();
            return Response.ok(BaseResponse.success("Found " + subscribers.size() + " matching jobs", 200, "/api/v1/test/test-matching-jobs"))
                    .build();
        } catch (Exception e) {
            log.error("Error testing job matching", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(BaseResponse.success("Error: " + e.getMessage(), 500, "/api/v1/test/test-matching-jobs"))
                    .build();
        }
    }
}
