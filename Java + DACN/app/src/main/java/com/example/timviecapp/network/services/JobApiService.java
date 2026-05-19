package com.example.timviecapp.network.services;

import com.example.timviecapp.models.common.ApiResponse;
import com.example.timviecapp.models.common.PaginationResponse;
import com.example.timviecapp.models.job.JobRequest;
import com.example.timviecapp.models.job.JobResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface JobApiService {

    @POST("jobs")
    Call<ApiResponse<JobResponse>> createJob(@Body JobRequest request);

    @GET("jobs/{id}")
    Call<ApiResponse<JobResponse>> getJobById(@Path("id") int id);

    @PUT("jobs/{id}")
    Call<ApiResponse<JobResponse>> updateJob(@Path("id") int id, @Body JobRequest request);

    @DELETE("jobs/{id}")
    Call<ApiResponse<Object>> deleteJob(@Path("id") int id);

    @PATCH("jobs/{id}/close")
    Call<ApiResponse<JobResponse>> closeJob(@Path("id") int id);

    @PATCH("jobs/{id}/reopen")
    Call<ApiResponse<JobResponse>> reopenJob(@Path("id") int id);

    @GET("jobs")
    Call<ApiResponse<PaginationResponse<JobResponse>>> getJobs(
            @Query("page") int page,
            @Query("size") int size,
            @Query("sortBy") String sortBy,
            @Query("direction") String direction,
            @Query("keyword") String keyword,
            @Query("filter") String filter
    );

    @GET("jobs/search")
    Call<ApiResponse<PaginationResponse<JobResponse>>> searchJobs(
            @Query("keyword") String keyword,
            @Query("skill") Integer skill,
            @Query("skills") String skills,
            @Query("salaryFrom") Double salaryFrom,
            @Query("salaryTo") Double salaryTo,
            @Query("location") String location,
            @Query("level") String level,
            @Query("page") int page,
            @Query("size") int size
    );

    @GET("jobs/company/{companyId}")
    Call<ApiResponse<PaginationResponse<JobResponse>>> getJobsByCompany(
            @Path("companyId") int companyId,
            @Query("page") int page,
            @Query("size") int size
    );

    @GET("jobs/skills/{skillId}")
    Call<ApiResponse<PaginationResponse<JobResponse>>> getJobsBySkill(
            @Path("skillId") int skillId,
            @Query("page") int page,
            @Query("size") int size
    );
}
