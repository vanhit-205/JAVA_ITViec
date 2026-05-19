package com.example.timviecapp.network.services;

import com.example.timviecapp.models.common.ApiResponse;
import com.example.timviecapp.models.common.PaginationResponse;
import com.example.timviecapp.models.subscriber.SubscriberRequest;
import com.example.timviecapp.models.subscriber.SubscriberResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface SubscriberApiService {

    @POST("subscribers")
    Call<ApiResponse<SubscriberResponse>> createSubscriber(@Body SubscriberRequest request);

    @GET("subscribers/{id}")
    Call<ApiResponse<SubscriberResponse>> getSubscriber(@Path("id") int id);

    @PUT("subscribers/{id}")
    Call<ApiResponse<SubscriberResponse>> updateSubscriber(@Path("id") int id, @Body SubscriberRequest request);

    @DELETE("subscribers/{id}")
    Call<ApiResponse<Object>> deleteSubscriber(@Path("id") int id);

    @PATCH("subscribers/{id}/enable")
    Call<ApiResponse<SubscriberResponse>> enableSubscriber(@Path("id") int id);

    @PATCH("subscribers/{id}/disable")
    Call<ApiResponse<SubscriberResponse>> disableSubscriber(@Path("id") int id);

    @GET("subscribers")
    Call<ApiResponse<PaginationResponse<SubscriberResponse>>> getSubscribers(
            @Query("page") int page,
            @Query("size") int size
    );
}
