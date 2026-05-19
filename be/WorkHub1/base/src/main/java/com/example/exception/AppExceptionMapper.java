package com.example.exception;

import com.example.domain.dto.response.ErrorResponse;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class AppExceptionMapper implements ExceptionMapper<AppException> {
    // resolve business
//    email đã tồn tại
//    user không tồn tại
//    job đã hết hạn
//    mật khẩu sai
    @Context
    ContainerRequestContext requestContext;

    @Override
    public Response toResponse(AppException ex) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErrorResponse(
                        ex.getCode(),
                        ex.getMessage(),
                        requestContext.getUriInfo().getPath()
                ))
                .build();
    }
}
