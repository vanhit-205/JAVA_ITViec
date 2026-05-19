package com.example.timviecapp.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.timviecapp.models.auth.ChangePasswordRequest;
import com.example.timviecapp.models.auth.VerifyEmailRequest;
import com.example.timviecapp.models.auth.VerifyOtpRequest;
import com.example.timviecapp.models.common.ApiResponse;
import com.example.timviecapp.network.RetrofitClient;
import com.example.timviecapp.network.services.ForgotPasswordApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordRepository {
    private final ForgotPasswordApiService apiService;

    public ForgotPasswordRepository() {
        apiService = RetrofitClient.getClient().create(ForgotPasswordApiService.class);
    }

    public LiveData<ApiResponse<Object>> verifyEmail(String email) {
        MutableLiveData<ApiResponse<Object>> data = new MutableLiveData<>();
        apiService.verifyEmail(new VerifyEmailRequest(email)).enqueue(new Callback<ApiResponse<Object>>() {
            @Override
            public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                if (response.isSuccessful()) data.setValue(response.body());
                else data.setValue(null);
            }

            @Override
            public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                data.setValue(null);
            }
        });
        return data;
    }

    public LiveData<ApiResponse<Object>> verifyOtp(String email, String otp) {
        MutableLiveData<ApiResponse<Object>> data = new MutableLiveData<>();
        apiService.verifyOtp(new VerifyOtpRequest(email, otp)).enqueue(new Callback<ApiResponse<Object>>() {
            @Override
            public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                if (response.isSuccessful()) data.setValue(response.body());
                else data.setValue(null);
            }

            @Override
            public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                data.setValue(null);
            }
        });
        return data;
    }

    public LiveData<ApiResponse<Object>> changePassword(String email, String otp, String newPassword) {
        MutableLiveData<ApiResponse<Object>> data = new MutableLiveData<>();
        apiService.changePassword(new ChangePasswordRequest(email, otp, newPassword)).enqueue(new Callback<ApiResponse<Object>>() {
            @Override
            public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                if (response.isSuccessful()) data.setValue(response.body());
                else data.setValue(null);
            }

            @Override
            public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                data.setValue(null);
            }
        });
        return data;
    }
}
