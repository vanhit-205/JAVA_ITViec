package com.example.timviecapp.network.services;

import com.example.timviecapp.models.auth.ChangePasswordRequest;
import com.example.timviecapp.models.auth.VerifyEmailRequest;
import com.example.timviecapp.models.auth.VerifyOtpRequest;
import com.example.timviecapp.models.common.ApiResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ForgotPasswordApiService {

    @POST("forgot-password/verify-email")
    Call<ApiResponse<Object>> verifyEmail(@Body VerifyEmailRequest request);

    @POST("forgot-password/verify-otp")
    Call<ApiResponse<Object>> verifyOtp(@Body VerifyOtpRequest request);

    @POST("forgot-password/change-password")
    Call<ApiResponse<Object>> changePassword(@Body ChangePasswordRequest request);
}
