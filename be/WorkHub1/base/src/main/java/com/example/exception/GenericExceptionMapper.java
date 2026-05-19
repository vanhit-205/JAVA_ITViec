package com.example.exception;

import com.example.domain.dto.response.ErrorResponse;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

@Provider
public class GenericExceptionMapper implements ExceptionMapper<Exception> {


    //resolve fallback/global
    // cac exception chua duoc xu li
//    NullPointerException
//            SQLException
//    RuntimeException lạ
//    bug hệ thống
    private static final Logger log = Logger.getLogger(GenericExceptionMapper.class);

    @Context
    ContainerRequestContext requestContext;

    @Override
    public Response toResponse(Exception ex) {

        log.error("Unhandled exception", ex);

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ErrorResponse(
                        9999,
                        "Internal Server Error",
                        requestContext.getUriInfo().getPath()
                ))
                .build();
    }
}
