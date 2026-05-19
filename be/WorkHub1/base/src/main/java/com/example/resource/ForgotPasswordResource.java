package com.example.resource;

import com.example.base.BaseResponse;
import com.example.domain.dto.request.ChangePasswordRequest;
import com.example.domain.dto.request.VerifyEmailRequest;
import com.example.domain.dto.request.VerifyOtpRequest;
import com.example.domain.dto.response.OtpResponse;
import com.example.service.ForgotPasswordService;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;

/**
 * Forgot Password API Resource
 *
 * Endpoints:
 * POST /api/v1/forgot-password/verify-email - Send OTP to email
 * POST /api/v1/forgot-password/verify-otp   - Verify OTP
 * POST /api/v1/forgot-password/change-password - Change password
 */
@Path("/api/v1/forgot-password")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ForgotPasswordResource {

    private static final Logger log = Logger.getLogger(ForgotPasswordResource.class);

    @Inject
    ForgotPasswordService forgotPasswordService;

    @Context
    HttpHeaders headers;

    @Inject
    Mailer mailer;

    @Path("/test-mail")
    @GET
    public String testMail() {

        mailer.send(
                Mail.withText(
                        "yourgmail@gmail.com",
                        "Test Mail",
                        "Hello from Quarkus"
                )
        );

        return "sent";
    }

    /**
     * Step 1: Verify email and send OTP
     *
     * POST /api/v1/forgot-password/verify-email
     *
     * Request Body:
     * {
     *   "email": "user@example.com"
     * }
     */
    @POST
    @Path("/verify-email")
    public Response verifyEmail(@Valid VerifyEmailRequest request) {
        log.info("Verify email request received for: " + request.email);

        String ipAddress = getClientIpAddress();

        OtpResponse response = forgotPasswordService.verifyEmail(request, ipAddress);

        return Response.ok(BaseResponse.success(response, 200, "/api/v1/forgot-password/verify-email"))
                .build();
    }

    /**
     * Step 2: Verify OTP
     *
     * POST /api/v1/forgot-password/verify-otp
     *
     * Request Body:
     * {
     *   "email": "user@example.com",
     *   "otp": "123456"
     * }
     */
    @POST
    @Path("/verify-otp")
    public Response verifyOtp(@Valid VerifyOtpRequest request) {
        log.info("Verify OTP request received for: " + request.email);

        String message = forgotPasswordService.verifyOtp(request);

        return Response.ok(BaseResponse.success(message, 200, "/api/v1/forgot-password/verify-otp"))
                .build();
    }

    /**
     * Step 3: Change password
     *
     * POST /api/v1/forgot-password/change-password
     *
     * Request Body:
     * {
     *   "email": "user@example.com",
     *   "otp": "123456",
     *   "newPassword": "newPassword123",
     *   "repeatPassword": "newPassword123"
     * }
     */
    @POST
    @Path("/change-password")
    public Response changePassword(@Valid ChangePasswordRequest request) {
        log.info("Change password request received for: " + request.email);

        String message = forgotPasswordService.changePassword(request);

        return Response.ok(BaseResponse.success(message, 200, "/api/v1/forgot-password/change-password"))
                .build();
    }

    /**
     * Extract client IP address
     */
    private String getClientIpAddress() {
        String xForwardedFor = headers.getHeaderString("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = headers.getHeaderString("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }

        return "unknown";
    }
}
