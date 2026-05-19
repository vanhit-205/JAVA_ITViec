package com.example.exception;

import com.example.domain.dto.response.ErrorResponse;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ForbiddenExceptionMapper implements ExceptionMapper<ForbiddenException> {

    @Context
    ContainerRequestContext requestContext;

    @Override
    public Response toResponse(ForbiddenException ex) {
        return Response.status(Response.Status.FORBIDDEN)
                .entity(new ErrorResponse(
                        ex.getCode(),
                        ex.getMessage(),
                        requestContext.getUriInfo().getPath()
                ))
                .build();
    }
}
