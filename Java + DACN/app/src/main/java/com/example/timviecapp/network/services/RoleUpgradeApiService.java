package com.example.timviecapp.network.services;

import com.example.timviecapp.models.common.ApiResponse;
import com.example.timviecapp.models.user.RoleUpgradeRequest;
import com.example.timviecapp.models.user.UpgradeRequestPayload;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface RoleUpgradeApiService {

    @POST("role-upgrades/request")
    Call<ApiResponse<RoleUpgradeRequest>> requestUpgrade(@Body UpgradeRequestPayload payload);

    @GET("role-upgrades/my-request")
    Call<ApiResponse<RoleUpgradeRequest>> getMyRequest();

    @GET("role-upgrades/list")
    Call<ApiResponse<java.util.List<RoleUpgradeRequest>>> getAllRequests();

    @retrofit2.http.PUT("role-upgrades/{id}/approve")
    Call<ApiResponse<Object>> approveRequest(@retrofit2.http.Path("id") int id);

    @retrofit2.http.PUT("role-upgrades/{id}/reject")
    Call<ApiResponse<Object>> rejectRequest(@retrofit2.http.Path("id") int id, @Body com.example.timviecapp.models.user.RejectPayload payload);
}
