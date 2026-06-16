package com.example.timviecapp.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.timviecapp.models.common.ApiResponse;
import com.example.timviecapp.models.common.PaginationResponse;
import com.example.timviecapp.models.subscriber.SubscriberRequest;
import com.example.timviecapp.models.subscriber.SubscriberResponse;
import com.example.timviecapp.network.RetrofitClient;
import com.example.timviecapp.network.services.SubscriberApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * SubscriberRepository - Quản lý đăng ký nhận thông báo việc làm
 * UC30: Tạo mới Subscriber
 * UC31: Cập nhật Subscriber
 */
public class SubscriberRepository {
    private final SubscriberApiService apiService;
    private static final String TAG = "SubscriberRepository";

    public SubscriberRepository() {
        apiService = RetrofitClient.getClient().create(SubscriberApiService.class);
    }

    /**
     * UC30: Đăng ký nhận thông báo việc làm
     */
    public LiveData<ApiResponse<SubscriberResponse>> createSubscriber(SubscriberRequest request) {
        MutableLiveData<ApiResponse<SubscriberResponse>> data = new MutableLiveData<>();

        apiService.createSubscriber(request).enqueue(new Callback<ApiResponse<SubscriberResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<SubscriberResponse>> call,
                                   Response<ApiResponse<SubscriberResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    data.setValue(response.body());
                } else {
                    Log.e(TAG, "createSubscriber failed: " + response.code());
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<SubscriberResponse>> call, Throwable t) {
                Log.e(TAG, "createSubscriber error: " + t.getMessage());
                data.setValue(null);
            }
        });

        return data;
    }

    /**
     * UC31: Cập nhật thông tin subscriber
     */
    public LiveData<ApiResponse<SubscriberResponse>> updateSubscriber(int id, SubscriberRequest request) {
        MutableLiveData<ApiResponse<SubscriberResponse>> data = new MutableLiveData<>();

        apiService.updateSubscriber(id, request).enqueue(new Callback<ApiResponse<SubscriberResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<SubscriberResponse>> call,
                                   Response<ApiResponse<SubscriberResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    data.setValue(response.body());
                } else {
                    Log.e(TAG, "updateSubscriber failed: " + response.code());
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<SubscriberResponse>> call, Throwable t) {
                Log.e(TAG, "updateSubscriber error: " + t.getMessage());
                data.setValue(null);
            }
        });

        return data;
    }

    /**
     * Hủy đăng ký nhận thông báo (Unsubscribe)
     */
    public LiveData<ApiResponse<SubscriberResponse>> disableSubscriber(int id) {
        MutableLiveData<ApiResponse<SubscriberResponse>> data = new MutableLiveData<>();

        apiService.disableSubscriber(id).enqueue(new Callback<ApiResponse<SubscriberResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<SubscriberResponse>> call,
                                   Response<ApiResponse<SubscriberResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    data.setValue(response.body());
                } else {
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<SubscriberResponse>> call, Throwable t) {
                Log.e(TAG, "disableSubscriber error: " + t.getMessage());
                data.setValue(null);
            }
        });

        return data;
    }

    /**
     * Lấy thông tin subscriber theo ID
     */
    public LiveData<ApiResponse<SubscriberResponse>> getSubscriber(int id) {
        MutableLiveData<ApiResponse<SubscriberResponse>> data = new MutableLiveData<>();

        apiService.getSubscriber(id).enqueue(new Callback<ApiResponse<SubscriberResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<SubscriberResponse>> call,
                                   Response<ApiResponse<SubscriberResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    data.setValue(response.body());
                } else {
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<SubscriberResponse>> call, Throwable t) {
                Log.e(TAG, "getSubscriber error: " + t.getMessage());
                data.setValue(null);
            }
        });

        return data;
    }

    /**
     * Lấy danh sách subscribers phân trang (Phục vụ Admin)
     */
    public LiveData<ApiResponse<PaginationResponse<SubscriberResponse>>> getSubscribers(int page, int size) {
        MutableLiveData<ApiResponse<PaginationResponse<SubscriberResponse>>> data = new MutableLiveData<>();
        apiService.getSubscribers(page, size).enqueue(new Callback<ApiResponse<PaginationResponse<SubscriberResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<PaginationResponse<SubscriberResponse>>> call,
                                   Response<ApiResponse<PaginationResponse<SubscriberResponse>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    data.setValue(response.body());
                } else {
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<PaginationResponse<SubscriberResponse>>> call, Throwable t) {
                Log.e(TAG, "getSubscribers error: " + t.getMessage());
                data.setValue(null);
            }
        });
        return data;
    }

    /**
     * Xóa mềm Subscriber (Chuyển deleted = true)
     */
    public LiveData<ApiResponse<Object>> deleteSubscriber(int id) {
        MutableLiveData<ApiResponse<Object>> data = new MutableLiveData<>();
        apiService.deleteSubscriber(id).enqueue(new Callback<ApiResponse<Object>>() {
            @Override
            public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    data.setValue(response.body());
                } else {
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                Log.e(TAG, "deleteSubscriber error: " + t.getMessage());
                data.setValue(null);
            }
        });
        return data;
    }

    /**
     * Kích hoạt lại Subscriber
     */
    public LiveData<ApiResponse<SubscriberResponse>> enableSubscriber(int id) {
        MutableLiveData<ApiResponse<SubscriberResponse>> data = new MutableLiveData<>();
        apiService.enableSubscriber(id).enqueue(new Callback<ApiResponse<SubscriberResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<SubscriberResponse>> call,
                                   Response<ApiResponse<SubscriberResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    data.setValue(response.body());
                } else {
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<SubscriberResponse>> call, Throwable t) {
                Log.e(TAG, "enableSubscriber error: " + t.getMessage());
                data.setValue(null);
            }
        });
        return data;
    }
}
