package com.example.timviecapp.network;

import retrofit2.Retrofit;

/**
 * RetrofitClient - Wrapper class delegating to the unified ApiClient
 * for backward compatibility with repositories.
 */
public class RetrofitClient {
    public static Retrofit getClient() {
        return ApiClient.getClient();
    }
}
