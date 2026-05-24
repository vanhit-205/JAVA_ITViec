package com.example.timviecapp.network.services;

import com.example.timviecapp.models.auth.UserResponse;
import com.example.timviecapp.models.common.ApiResponse;
import com.example.timviecapp.models.common.PaginationResponse;
import com.example.timviecapp.models.user.CreateUserRequest;
import com.example.timviecapp.models.user.UpdateUserRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UserApiService {

    @POST("users")
    Call<ApiResponse<UserResponse>> createUser(@Body CreateUserRequest request);

    @GET("users/{id}")
    Call<ApiResponse<UserResponse>> getUserById(@Path("id") int id);

    @PUT("users/{id}")
    Call<ApiResponse<UserResponse>> updateUser(@Path("id") int id, @Body UpdateUserRequest request);

    @DELETE("users/{id}")
    Call<ApiResponse<Object>> deleteUser(@Path("id") int id);

    @PATCH("users/{id}/lock")
    Call<ApiResponse<UserResponse>> lockUser(@Path("id") int id);

    @PATCH("users/{id}/unlock")
    Call<ApiResponse<UserResponse>> unlockUser(@Path("id") int id);

    @PATCH("users/{id}/disable")
    Call<ApiResponse<UserResponse>> disableUser(@Path("id") int id);

    @PATCH("users/{id}/enable")
    Call<ApiResponse<UserResponse>> enableUser(@Path("id") int id);

    @GET("users")
    Call<ApiResponse<PaginationResponse<UserResponse>>> getUsers(
            @Query("page") int page,
            @Query("size") int size,
            @Query("sortBy") String sortBy,
            @Query("direction") String direction,
            @Query("keyword") String keyword,
            @Query("filter") String filter
    );
}
