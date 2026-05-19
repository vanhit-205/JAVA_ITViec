package com.example.resource;

import com.example.base.BaseResponse;
import com.example.domain.dto.request.SkillCreateRequest;
import com.example.domain.dto.request.SkillUpdateRequest;
import com.example.domain.dto.response.SkillResponse;
import com.example.pagination.PageRequest;
import com.example.pagination.PageResponse;
import com.example.service.SkillService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;

@Path("/api/v1/skills")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SkillResource {

    private static final Logger log = Logger.getLogger(SkillResource.class);

    @Inject
    SkillService skillService;

    @POST
    public Response create(@Valid SkillCreateRequest request) {
        log.info("Create skill request");
        SkillResponse skill = skillService.create(request);
        return Response.status(Response.Status.CREATED)
                .entity(BaseResponse.success(skill, 201, "/api/v1/skills"))
                .build();
    }

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Long id) {
        log.info("Get skill by ID: " + id);
        SkillResponse skill = skillService.getById(id);
        return Response.ok(BaseResponse.success(skill, 200, "/api/v1/skills/" + id))
                .build();
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, @Valid SkillUpdateRequest request) {
        log.info("Update skill: " + id);
        SkillResponse skill = skillService.update(id, request);
        return Response.ok(BaseResponse.success(skill, 200, "/api/v1/skills/" + id))
                .build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        log.info("Delete skill: " + id);
        skillService.delete(id);
        return Response.ok(BaseResponse.success("Skill deleted successfully", 200, "/api/v1/skills/" + id))
                .build();
    }

    @GET
    public Response getAll(
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("size") @DefaultValue("10") int size,
            @QueryParam("sortBy") @DefaultValue("createdAt") String sortBy,
            @QueryParam("direction") @DefaultValue("DESC") String direction,
            @QueryParam("keyword") String keyword,
            @QueryParam("filter") String filter) {

        log.info("Get all skills - page: " + page + ", size: " + size);

        PageRequest pageRequest = new PageRequest();
        pageRequest.setPage(page);
        pageRequest.setSize(size);
        pageRequest.setSortBy(sortBy);
        pageRequest.setDirection(direction);
        pageRequest.setKeyword(keyword);
        pageRequest.setFilter(filter);

        PageResponse<SkillResponse> response = skillService.getAll(pageRequest);
        return Response.ok(BaseResponse.success(response, 200, "/api/v1/skills"))
                .build();
    }
}