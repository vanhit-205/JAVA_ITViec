package com.example.timviecapp.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.timviecapp.models.auth.UserResponse;
import com.example.timviecapp.models.common.ApiResponse;
import com.example.timviecapp.models.common.PaginationResponse;
import com.example.timviecapp.models.user.UpdateUserRequest;
import com.example.timviecapp.network.RetrofitClient;
import com.example.timviecapp.network.services.UserApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRepository {
    private final UserApiService apiService;
    private static final String TAG = "UserRepository";

    public UserRepository() {
        apiService = RetrofitClient.getClient().create(UserApiService.class);
    }

    public LiveData<ApiResponse<UserResponse>> getUserById(int id) {
        MutableLiveData<ApiResponse<UserResponse>> data = new MutableLiveData<>();
        apiService.getUserById(id).enqueue(new Callback<ApiResponse<UserResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<UserResponse>> call, Response<ApiResponse<UserResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    data.setValue(response.body());
                } else {
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<UserResponse>> call, Throwable t) {
                Log.e(TAG, "getUserById error: " + t.getMessage());
                data.setValue(null);
            }
        });
        return data;
    }

    public LiveData<ApiResponse<UserResponse>> updateUser(int id, UpdateUserRequest request) {
        MutableLiveData<ApiResponse<UserResponse>> data = new MutableLiveData<>();
        apiService.updateUser(id, request).enqueue(new Callback<ApiResponse<UserResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<UserResponse>> call, Response<ApiResponse<UserResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    data.setValue(response.body());
                } else {
                    Log.e(TAG, "updateUser failed: " + response.code());
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<UserResponse>> call, Throwable t) {
                Log.e(TAG, "updateUser error: " + t.getMessage());
                data.setValue(null);
            }
        });
        return data;
    }

    public LiveData<ApiResponse<PaginationResponse<UserResponse>>> getUsers(int page, int size, String keyword) {
        MutableLiveData<ApiResponse<PaginationResponse<UserResponse>>> data = new MutableLiveData<>();
        apiService.getUsers(page, size, "id", "asc", keyword, null)
                .enqueue(new Callback<ApiResponse<PaginationResponse<UserResponse>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<PaginationResponse<UserResponse>>> call,
                                           Response<ApiResponse<PaginationResponse<UserResponse>>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            data.setValue(response.body());
                        } else {
                            data.setValue(null);
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<PaginationResponse<UserResponse>>> call, Throwable t) {
                        data.setValue(null);
                    }
                });
        return data;
    }

    public LiveData<ApiResponse<UserResponse>> lockUser(int id) {
        MutableLiveData<ApiResponse<UserResponse>> data = new MutableLiveData<>();
        apiService.lockUser(id).enqueue(new Callback<ApiResponse<UserResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<UserResponse>> call, Response<ApiResponse<UserResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    data.setValue(response.body());
                } else {
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<UserResponse>> call, Throwable t) {
                data.setValue(null);
            }
        });
        return data;
    }

    public LiveData<ApiResponse<UserResponse>> unlockUser(int id) {
        MutableLiveData<ApiResponse<UserResponse>> data = new MutableLiveData<>();
        apiService.unlockUser(id).enqueue(new Callback<ApiResponse<UserResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<UserResponse>> call, Response<ApiResponse<UserResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    data.setValue(response.body());
                } else {
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<UserResponse>> call, Throwable t) {
                data.setValue(null);
            }
        });
        return data;
    }

    public LiveData<ApiResponse<UserResponse>> disableUser(int id) {
        MutableLiveData<ApiResponse<UserResponse>> data = new MutableLiveData<>();
        apiService.disableUser(id).enqueue(new Callback<ApiResponse<UserResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<UserResponse>> call, Response<ApiResponse<UserResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    data.setValue(response.body());
                } else {
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<UserResponse>> call, Throwable t) {
                data.setValue(null);
            }
        });
        return data;
    }

    public LiveData<ApiResponse<UserResponse>> enableUser(int id) {
        MutableLiveData<ApiResponse<UserResponse>> data = new MutableLiveData<>();
        apiService.enableUser(id).enqueue(new Callback<ApiResponse<UserResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<UserResponse>> call, Response<ApiResponse<UserResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    data.setValue(response.body());
                } else {
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<UserResponse>> call, Throwable t) {
                data.setValue(null);
            }
        });
        return data;
    }
}
