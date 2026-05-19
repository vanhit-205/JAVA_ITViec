package com.example.timviecapp.network.services;

import com.example.timviecapp.models.auth.LoginRequest;
import com.example.timviecapp.models.auth.LoginResponse;
import com.example.timviecapp.models.auth.LogoutRequest;
import com.example.timviecapp.models.auth.RefreshResponse;
import com.example.timviecapp.models.auth.RefreshTokenRequest;
import com.example.timviecapp.models.auth.RegisterRequest;
import com.example.timviecapp.models.auth.UserResponse;
import com.example.timviecapp.models.common.ApiResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface AuthApiService {

    @POST("auth/register")
    Call<ApiResponse<UserResponse>> register(@Body RegisterRequest request);

    @POST("auth/login")
    Call<ApiResponse<LoginResponse>> login(@Body LoginRequest request);

    @POST("auth/refresh")
    Call<ApiResponse<RefreshResponse>> refreshToken(@Body RefreshTokenRequest request);

    @GET("auth/me")
    Call<ApiResponse<UserResponse>> getMe();

    @POST("auth/logout")
    Call<ApiResponse<Object>> logout(@Body LogoutRequest request);
}
