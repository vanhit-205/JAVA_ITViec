package com.example.timviecapp.network.services;

import com.example.timviecapp.models.common.ApiResponse;
import com.example.timviecapp.models.common.PaginationResponse;
import com.example.timviecapp.models.resume.ResumeRequest;
import com.example.timviecapp.models.resume.ResumeResponse;
import com.example.timviecapp.models.resume.ResumeStatusRequest;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ResumeApiService {

    @POST("resumes")
    Call<ApiResponse<ResumeResponse>> createResume(@Body ResumeRequest request);

    @Multipart
    @POST("resumes")
    Call<ApiResponse<ResumeResponse>> uploadResume(
            @Part("email") RequestBody email,
            @Part("userId") RequestBody userId,
            @Part("jobId") RequestBody jobId,
            @Part MultipartBody.Part file
    );

    @GET("resumes/{id}")
    Call<ApiResponse<ResumeResponse>> getResumeById(@Path("id") int id);

    @PUT("resumes/{id}")
    Call<ApiResponse<ResumeResponse>> updateResume(@Path("id") int id, @Body ResumeRequest request);

    @DELETE("resumes/{id}")
    Call<ApiResponse<Object>> deleteResume(@Path("id") int id);

    @PATCH("resumes/{id}/status")
    Call<ApiResponse<ResumeResponse>> updateResumeStatus(@Path("id") int id, @Body ResumeStatusRequest request);

    @GET("resumes")
    Call<ApiResponse<PaginationResponse<ResumeResponse>>> getResumes(
            @Query("page") int page,
            @Query("size") int size
    );

    @GET("resumes/my")
    Call<ApiResponse<PaginationResponse<ResumeResponse>>> getMyResumes(
            @Query("page") int page,
            @Query("size") int size
    );

    @GET("resumes/company/{companyId}")
    Call<ApiResponse<PaginationResponse<ResumeResponse>>> getCompanyResumes(
            @Path("companyId") int companyId,
            @Query("page") int page,
            @Query("size") int size
    );
}
