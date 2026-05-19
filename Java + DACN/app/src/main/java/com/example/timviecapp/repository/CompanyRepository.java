package com.example.timviecapp.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.timviecapp.models.common.ApiResponse;
import com.example.timviecapp.models.common.PaginationResponse;
import com.example.timviecapp.models.company.CompanyResponse;
import com.example.timviecapp.network.RetrofitClient;
import com.example.timviecapp.network.services.CompanyApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * CompanyRepository - Xử lý dữ liệu công ty
 * UC9: Xem danh sách công ty
 * UC10: Xem chi tiết công ty
 */
public class CompanyRepository {
    private final CompanyApiService apiService;
    private static final String TAG = "CompanyRepository";

    public CompanyRepository() {
        apiService = RetrofitClient.getClient().create(CompanyApiService.class);
    }

    /**
     * UC9: Lấy danh sách công ty có phân trang
     */
    public LiveData<ApiResponse<PaginationResponse<CompanyResponse>>> getCompanies(int page, int size) {
        MutableLiveData<ApiResponse<PaginationResponse<CompanyResponse>>> data = new MutableLiveData<>();

        apiService.getCompanies(page, size, "createdAt", "desc", null, null)
                .enqueue(new Callback<ApiResponse<PaginationResponse<CompanyResponse>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<PaginationResponse<CompanyResponse>>> call,
                                           Response<ApiResponse<PaginationResponse<CompanyResponse>>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            data.setValue(response.body());
                        } else {
                            Log.e(TAG, "getCompanies failed: " + response.code());
                            data.setValue(null);
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<PaginationResponse<CompanyResponse>>> call, Throwable t) {
                        Log.e(TAG, "getCompanies error: " + t.getMessage());
                        data.setValue(null);
                    }
                });

        return data;
    }

    /**
     * UC10: Lấy chi tiết công ty theo ID
     */
    public LiveData<ApiResponse<CompanyResponse>> getCompanyById(int id) {
        MutableLiveData<ApiResponse<CompanyResponse>> data = new MutableLiveData<>();

        apiService.getCompanyById(id).enqueue(new Callback<ApiResponse<CompanyResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<CompanyResponse>> call,
                                   Response<ApiResponse<CompanyResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    data.setValue(response.body());
                } else {
                    Log.e(TAG, "getCompanyById failed: " + response.code());
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<CompanyResponse>> call, Throwable t) {
                Log.e(TAG, "getCompanyById error: " + t.getMessage());
                data.setValue(null);
            }
        });

        return data;
    }

    /**
     * Tạo công ty mới
     */
    public LiveData<ApiResponse<CompanyResponse>> createCompany(com.example.timviecapp.models.company.CompanyRequest request) {
        MutableLiveData<ApiResponse<CompanyResponse>> data = new MutableLiveData<>();

        apiService.createCompany(request).enqueue(new Callback<ApiResponse<CompanyResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<CompanyResponse>> call,
                                   Response<ApiResponse<CompanyResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    data.setValue(response.body());
                } else {
                    Log.e(TAG, "createCompany failed: " + response.code());
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<CompanyResponse>> call, Throwable t) {
                Log.e(TAG, "createCompany error: " + t.getMessage());
                data.setValue(null);
            }
        });

        return data;
    }

    public LiveData<ApiResponse<CompanyResponse>> updateCompany(int id, com.example.timviecapp.models.company.CompanyRequest request) {
        MutableLiveData<ApiResponse<CompanyResponse>> data = new MutableLiveData<>();
        apiService.updateCompany(id, request).enqueue(new Callback<ApiResponse<CompanyResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<CompanyResponse>> call, Response<ApiResponse<CompanyResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    data.setValue(response.body());
                } else {
                    Log.e(TAG, "updateCompany failed: " + response.code());
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<CompanyResponse>> call, Throwable t) {
                Log.e(TAG, "updateCompany error: " + t.getMessage());
                data.setValue(null);
            }
        });
        return data;
    }
}
