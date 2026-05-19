package com.example.exception;

import com.example.domain.dto.response.ErrorResponse;
import com.example.service.TokenBlacklistService;
import io.quarkus.security.ForbiddenException;
import io.smallrye.jwt.auth.principal.JWTParser;
import io.smallrye.jwt.auth.principal.ParseException;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.logging.Logger;

@Provider
public class SecurityExceptionMapper implements ExceptionMapper<RuntimeException> {

    private static final Logger log = Logger.getLogger(SecurityExceptionMapper.class);

    @Context
    ContainerRequestContext requestContext;

    @Inject
    TokenBlacklistService tokenBlacklistService;

    @Override
    public Response toResponse(RuntimeException ex) {

        if (ex instanceof NotAuthorizedException) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(new ErrorResponse(
                            1003,
                            "Unauthorized - Invalid or missing token",
                            requestContext.getUriInfo().getPath()
                    ))
                    .build();
        }

        if (ex instanceof ForbiddenException) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(new ErrorResponse(
                            1004,
                            "Forbidden - Access denied",
                            requestContext.getUriInfo().getPath()
                    ))
                    .build();
        }

        // Log and return generic error for other runtime exceptions
        log.error("Unhandled security exception: " + ex.getClass().getName(), ex);

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ErrorResponse(
                        9999,
                        "Internal server error",
                        requestContext.getUriInfo().getPath()
                ))
                .build();
    }
}
