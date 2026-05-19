package com.example;

import com.example.base.BaseResponse;
import com.example.exception.GlobalExceptionHandler;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.UriInfo;
import org.jboss.logging.Logger;


@Path("/hello")
public class ExampleResource {

    private static final Logger log = Logger.getLogger(ExampleResource.class);

    //can inject uriInfo
    @Context
    UriInfo uriInfo;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public BaseResponse<String> hello() {

        String path = uriInfo.getPath();

        return BaseResponse.success(
                "Hello Quarkus",
                200,
                path
        );
    }
}
