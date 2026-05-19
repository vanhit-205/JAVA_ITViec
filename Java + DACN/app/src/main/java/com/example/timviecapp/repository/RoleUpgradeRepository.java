package com.example.timviecapp.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.timviecapp.models.common.ApiResponse;
import com.example.timviecapp.models.user.RoleUpgradeRequest;
import com.example.timviecapp.models.user.UpgradeRequestPayload;
import com.example.timviecapp.network.RetrofitClient;
import com.example.timviecapp.network.services.RoleUpgradeApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RoleUpgradeRepository {
    private final RoleUpgradeApiService apiService;
    private static final String TAG = "RoleUpgradeRepository";

    public RoleUpgradeRepository() {
        apiService = RetrofitClient.getClient().create(RoleUpgradeApiService.class);
    }

    public LiveData<ApiResponse<RoleUpgradeRequest>> requestUpgrade(String newCompanyName, String reason) {
        MutableLiveData<ApiResponse<RoleUpgradeRequest>> data = new MutableLiveData<>();
        UpgradeRequestPayload payload = new UpgradeRequestPayload(null, newCompanyName, reason);
        apiService.requestUpgrade(payload).enqueue(new Callback<ApiResponse<RoleUpgradeRequest>>() {
            @Override
            public void onResponse(Call<ApiResponse<RoleUpgradeRequest>> call, Response<ApiResponse<RoleUpgradeRequest>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    data.setValue(response.body());
                } else {
                    Log.e(TAG, "requestUpgrade failed: " + response.code());
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<RoleUpgradeRequest>> call, Throwable t) {
                Log.e(TAG, "requestUpgrade error: " + t.getMessage());
                data.setValue(null);
            }
        });
        return data;
    }

    public LiveData<ApiResponse<RoleUpgradeRequest>> getMyRequest() {
        MutableLiveData<ApiResponse<RoleUpgradeRequest>> data = new MutableLiveData<>();
        apiService.getMyRequest().enqueue(new Callback<ApiResponse<RoleUpgradeRequest>>() {
            @Override
            public void onResponse(Call<ApiResponse<RoleUpgradeRequest>> call, Response<ApiResponse<RoleUpgradeRequest>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    data.setValue(response.body());
                } else {
                    Log.e(TAG, "getMyRequest failed: " + response.code());
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<RoleUpgradeRequest>> call, Throwable t) {
                Log.e(TAG, "getMyRequest error: " + t.getMessage());
                data.setValue(null);
            }
        });
        return data;
    }

    public LiveData<ApiResponse<java.util.List<RoleUpgradeRequest>>> getAllRequests() {
        MutableLiveData<ApiResponse<java.util.List<RoleUpgradeRequest>>> data = new MutableLiveData<>();
        apiService.getAllRequests().enqueue(new Callback<ApiResponse<java.util.List<RoleUpgradeRequest>>>() {
            @Override
            public void onResponse(Call<ApiResponse<java.util.List<RoleUpgradeRequest>>> call, Response<ApiResponse<java.util.List<RoleUpgradeRequest>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    data.setValue(response.body());
                } else {
                    Log.e(TAG, "getAllRequests failed: " + response.code());
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<java.util.List<RoleUpgradeRequest>>> call, Throwable t) {
                Log.e(TAG, "getAllRequests error: " + t.getMessage());
                data.setValue(null);
            }
        });
        return data;
    }

    public LiveData<ApiResponse<Object>> approveRequest(int id) {
        MutableLiveData<ApiResponse<Object>> data = new MutableLiveData<>();
        apiService.approveRequest(id).enqueue(new Callback<ApiResponse<Object>>() {
            @Override
            public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    data.setValue(response.body());
                } else {
                    Log.e(TAG, "approveRequest failed: " + response.code());
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                Log.e(TAG, "approveRequest error: " + t.getMessage());
                data.setValue(null);
            }
        });
        return data;
    }

    public LiveData<ApiResponse<Object>> rejectRequest(int id, String adminNotes) {
        MutableLiveData<ApiResponse<Object>> data = new MutableLiveData<>();
        com.example.timviecapp.models.user.RejectPayload payload = new com.example.timviecapp.models.user.RejectPayload(adminNotes);
        apiService.rejectRequest(id, payload).enqueue(new Callback<ApiResponse<Object>>() {
            @Override
            public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    data.setValue(response.body());
                } else {
                    Log.e(TAG, "rejectRequest failed: " + response.code());
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                Log.e(TAG, "rejectRequest error: " + t.getMessage());
                data.setValue(null);
            }
        });
        return data;
    }
}
