package com.example.exception;

import com.example.domain.dto.response.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ValidationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

    //resolve validate
    @Context
    ContainerRequestContext requestContext;

    @Override
    public Response toResponse(ConstraintViolationException ex) {

        String message = ex.getConstraintViolations()
                .stream()
                .findFirst()
                .map(v -> v.getMessage())
                .orElse("Validation error");

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErrorResponse(
                        1002,
                        message,
                        requestContext.getUriInfo().getPath()
                ))
                .build();
    }
}