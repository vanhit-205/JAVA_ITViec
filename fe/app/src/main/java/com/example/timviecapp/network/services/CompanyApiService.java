package com.example.timviecapp.network.services;

import com.example.timviecapp.models.common.ApiResponse;
import com.example.timviecapp.models.common.PaginationResponse;
import com.example.timviecapp.models.company.CompanyRequest;
import com.example.timviecapp.models.company.CompanyResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface CompanyApiService {

    @POST("companies")
    Call<ApiResponse<CompanyResponse>> createCompany(@Body CompanyRequest request);

    @GET("companies/{id}")
    Call<ApiResponse<CompanyResponse>> getCompanyById(@Path("id") int id);

    @PUT("companies/{id}")
    Call<ApiResponse<CompanyResponse>> updateCompany(@Path("id") int id, @Body CompanyRequest request);

    @DELETE("companies/{id}")
    Call<ApiResponse<Object>> deleteCompany(@Path("id") int id);

    @GET("companies")
    Call<ApiResponse<PaginationResponse<CompanyResponse>>> getCompanies(
            @Query("page") int page,
            @Query("size") int size,
            @Query("sortBy") String sortBy,
            @Query("direction") String direction,
            @Query("keyword") String keyword,
            @Query("filter") String filter
    );
}
