package com.example.timviecapp.repository;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.timviecapp.models.auth.LoginRequest;
import com.example.timviecapp.models.auth.LoginResponse;
import com.example.timviecapp.models.auth.RegisterRequest;
import com.example.timviecapp.models.auth.UserResponse;
import com.example.timviecapp.models.common.ApiResponse;
import com.example.timviecapp.network.RetrofitClient;
import com.example.timviecapp.network.services.AuthApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthRepository {
    private final AuthApiService authApiService;
    private static final String TAG = "AuthRepository";

    public AuthRepository() {
        authApiService = RetrofitClient.getClient().create(AuthApiService.class);
    }

    public LiveData<ApiResponse<LoginResponse>> login(String email, String password) {
        MutableLiveData<ApiResponse<LoginResponse>> loginData = new MutableLiveData<>();
        
        authApiService.login(new LoginRequest(email, password)).enqueue(new Callback<ApiResponse<LoginResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<LoginResponse>> call, Response<ApiResponse<LoginResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    loginData.setValue(response.body());
                } else {
                    Log.e(TAG, "Login failed. Code: " + response.code());
                    ApiResponse<LoginResponse> errorResponse = new ApiResponse<>();
                    errorResponse.setSuccess(false);
                    try {
                        if (response.errorBody() != null) {
                            String errorStr = response.errorBody().string();
                            Log.e(TAG, "Login Error body: " + errorStr);
                            
                            // Parse the flat JSON directly into the client-side ErrorResponse class
                            com.example.timviecapp.models.common.ErrorResponse clientError = 
                                    new com.google.gson.Gson().fromJson(errorStr, com.example.timviecapp.models.common.ErrorResponse.class);
                            errorResponse.setError(clientError);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    loginData.setValue(errorResponse);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<LoginResponse>> call, Throwable t) {
                Log.e(TAG, "Login connection error: " + t.getMessage());
                loginData.setValue(null);
            }
        });
        
        return loginData;
    }

    public LiveData<ApiResponse<UserResponse>> register(String email, String password, String name, String role) {
        MutableLiveData<ApiResponse<UserResponse>> registerData = new MutableLiveData<>();

        authApiService.register(new RegisterRequest(email, password, name, role)).enqueue(new Callback<ApiResponse<UserResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<UserResponse>> call, Response<ApiResponse<UserResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    registerData.setValue(response.body());
                } else {
                    Log.e(TAG, "Register failed. Code: " + response.code());
                    try {
                        if (response.errorBody() != null) {
                            Log.e(TAG, "Register Error body: " + response.errorBody().string());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    registerData.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<UserResponse>> call, Throwable t) {
                Log.e(TAG, "Register connection error: " + t.getMessage());
                registerData.setValue(null);
            }
        });

        return registerData;
    }

    public LiveData<ApiResponse<UserResponse>> getMe() {
        MutableLiveData<ApiResponse<UserResponse>> userData = new MutableLiveData<>();
        authApiService.getMe().enqueue(new Callback<ApiResponse<UserResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<UserResponse>> call, Response<ApiResponse<UserResponse>> response) {
                if (response.isSuccessful()) {
                    userData.setValue(response.body());
                } else {
                    userData.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<UserResponse>> call, Throwable t) {
                userData.setValue(null);
            }
        });
        return userData;
    }
}
