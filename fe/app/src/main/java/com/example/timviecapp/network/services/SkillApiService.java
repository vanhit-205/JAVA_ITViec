package com.example.timviecapp.network.services;

import com.example.timviecapp.models.common.ApiResponse;
import com.example.timviecapp.models.common.PaginationResponse;
import com.example.timviecapp.models.skill.SkillRequest;
import com.example.timviecapp.models.skill.SkillResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface SkillApiService {

    @POST("skills")
    Call<ApiResponse<SkillResponse>> createSkill(@Body SkillRequest request);

    @GET("skills/{id}")
    Call<ApiResponse<SkillResponse>> getSkillById(@Path("id") int id);

    @PUT("skills/{id}")
    Call<ApiResponse<SkillResponse>> updateSkill(@Path("id") int id, @Body SkillRequest request);

    @DELETE("skills/{id}")
    Call<ApiResponse<Object>> deleteSkill(@Path("id") int id);

    @GET("skills")
    Call<ApiResponse<PaginationResponse<SkillResponse>>> getSkills(
            @Query("page") int page,
            @Query("size") int size,
            @Query("sortBy") String sortBy,
            @Query("direction") String direction,
            @Query("keyword") String keyword,
            @Query("filter") String filter
    );
}
