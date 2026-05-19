package com.example.resource;

import com.example.base.BaseResponse;
import com.example.domain.dto.request.LoginRequest;
import com.example.domain.dto.request.LogoutRequest;
import com.example.domain.dto.request.RefreshTokenRequest;
import com.example.domain.dto.request.RegisterRequest;
import com.example.domain.dto.response.LoginResponse;
import com.example.domain.dto.response.TokenResponse;
import com.example.domain.dto.response.UserResponse;
import com.example.security.SecurityContext;
import com.example.service.AuthService;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;

@Path("/api/v1/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {

    private static final Logger log = Logger.getLogger(AuthResource.class);

    @Inject
    AuthService authService;

    @Inject
    SecurityContext securityContext;

    @Context
    HttpHeaders headers;

    /**
     * Register new user
     * POST /api/v1/auth/register
     */
    @POST
    @Path("/register")
    @PermitAll
    public Response register(@Valid RegisterRequest request) {
        log.info("Register request received for: " + request.email);

        UserResponse user = authService.register(request);

        return Response.status(Response.Status.CREATED)
                .entity(BaseResponse.success(user, 201, "/api/v1/auth/register"))
                .build();
    }

    /**
     * Login user
     * POST /api/v1/auth/login
     */
    @POST
    @Path("/login")
    @PermitAll
    public Response login(@Valid LoginRequest request) {
        log.info("Login request received for: " + request.email);

        String ipAddress = getClientIpAddress();
        String userAgent = headers.getHeaderString("User-Agent");

        LoginResponse loginResponse = authService.login(request, ipAddress, userAgent);

        return Response.ok(BaseResponse.success(loginResponse, 200, "/api/v1/auth/login"))
                .build();
    }

    /**
     * Refresh access token
     * POST /api/v1/auth/refresh
     */
    @POST
    @Path("/refresh")
    @PermitAll
    public Response refreshToken(@Valid RefreshTokenRequest request) {
        log.info("Token refresh request received");

        TokenResponse tokenResponse = authService.refreshToken(request);

        return Response.ok(BaseResponse.success(tokenResponse, 200, "/api/v1/auth/refresh"))
                .build();
    }

    /**
     * Logout user
     * POST /api/v1/auth/logout
     */
    @POST
    @Path("/logout")
    @RolesAllowed({"ROLE_ADMIN", "ROLE_RECRUITER", "ROLE_CANDIDATE"})
    public Response logout(LogoutRequest request) {
        log.info("Logout request received");

        authService.logout(request);

        return Response.ok(BaseResponse.success(null, 200, "/api/v1/auth/logout"))
                .build();
    }

    /**
     * Get current user info
     * GET /api/v1/auth/me
     */
    @GET
    @Path("/me")
    @RolesAllowed({"ROLE_ADMIN", "ROLE_RECRUITER", "ROLE_CANDIDATE"})
    public Response getCurrentUser() {
        log.info("Get current user request received");

        Long userId = securityContext.getCurrentUserId();
        UserResponse user = authService.getCurrentUser(userId);

        return Response.ok(BaseResponse.success(user, 200, "/api/v1/auth/me"))
                .build();
    }

    /**
     * Extract client IP address from request
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
