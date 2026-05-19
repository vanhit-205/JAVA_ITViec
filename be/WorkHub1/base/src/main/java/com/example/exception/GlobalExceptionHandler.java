package com.example.exception;

import com.example.domain.dto.response.ErrorResponse;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class GlobalExceptionHandler implements ExceptionMapper<Exception> {

    @Context
    ContainerRequestContext requestContext;

    @Override
    public Response toResponse(Exception exception) {

        if (exception instanceof AppException ex) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(
                            ex.getCode(),
                            ex.getMessage(),
                            requestContext.getUriInfo().getPath()
                    ))
                    .build();
        }

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ErrorResponse(
                        500,
                        "Internal Server Error",
                        requestContext.getUriInfo().getPath()
                ))
                .build();
    }
}
