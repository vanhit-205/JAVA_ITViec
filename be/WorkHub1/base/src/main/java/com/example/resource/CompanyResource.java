package com.example.resource;

import com.example.base.BaseResponse;
import com.example.domain.dto.request.CompanyCreateRequest;
import com.example.domain.dto.request.CompanyUpdateRequest;
import com.example.domain.dto.response.CompanyResponse;
import com.example.pagination.PageRequest;
import com.example.pagination.PageResponse;
import com.example.service.CompanyService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;

@Path("/api/v1/companies")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CompanyResource {

    private static final Logger log = Logger.getLogger(CompanyResource.class);

    @Inject
    CompanyService companyService;

    /**
     * Create new company
     * POST /api/v1/companies
     */
    @POST
    public Response create(@Valid CompanyCreateRequest request) {
        log.info("Create company request");
        CompanyResponse company = companyService.create(request);
        return Response.status(Response.Status.CREATED)
                .entity(BaseResponse.success(company, 201, "/api/v1/companies"))
                .build();
    }

    /**
     * Get company by ID
     * GET /api/v1/companies/{id}
     */
    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Long id) {
        log.info("Get company by ID: " + id);
        CompanyResponse company = companyService.getById(id);
        return Response.ok(BaseResponse.success(company, 200, "/api/v1/companies/" + id))
                .build();
    }

    /**
     * Update company
     * PUT /api/v1/companies/{id}
     */
    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, @Valid CompanyUpdateRequest request) {
        log.info("Update company: " + id);
        CompanyResponse company = companyService.update(id, request);
        return Response.ok(BaseResponse.success(company, 200, "/api/v1/companies/" + id))
                .build();
    }

    /**
     * Soft delete company
     * DELETE /api/v1/companies/{id}
     */
    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        log.info("Delete company: " + id);
        companyService.delete(id);
        return Response.ok(BaseResponse.success("Company deleted successfully", 200, "/api/v1/companies/" + id))
                .build();
    }

    /**
     * List companies with pagination and filter
     * GET /api/v1/companies
     */
    @GET
    public Response getAll(
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("size") @DefaultValue("10") int size,
            @QueryParam("sortBy") @DefaultValue("createdAt") String sortBy,
            @QueryParam("direction") @DefaultValue("DESC") String direction,
            @QueryParam("keyword") String keyword,
            @QueryParam("filter") String filter) {

        log.info("Get all companies - page: " + page + ", size: " + size);

        PageRequest pageRequest = new PageRequest();
        pageRequest.setPage(page);
        pageRequest.setSize(size);
        pageRequest.setSortBy(sortBy);
        pageRequest.setDirection(direction);
        pageRequest.setKeyword(keyword);
        pageRequest.setFilter(filter);

        PageResponse<CompanyResponse> response = companyService.getAll(pageRequest);
        return Response.ok(BaseResponse.success(response, 200, "/api/v1/companies"))
                .build();
    }
}
