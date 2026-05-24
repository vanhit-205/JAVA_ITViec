package com.example.timviecapp.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.timviecapp.models.common.ApiResponse;
import com.example.timviecapp.models.common.PaginationResponse;
import com.example.timviecapp.models.resume.ResumeRequest;
import com.example.timviecapp.models.resume.ResumeResponse;
import com.example.timviecapp.models.resume.ResumeStatusRequest;
import com.example.timviecapp.network.RetrofitClient;
import com.example.timviecapp.network.services.ResumeApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * ResumeRepository - Quản lý hồ sơ ứng tuyển
 * UC24: Tạo mới resume (ứng tuyển)
 * UC25: Cập nhật resume
 * UC26: Xóa resume
 * UC27: Xem chi tiết resume
 * UC29: Lấy danh sách resume theo user
 */
public class ResumeRepository {
    private final ResumeApiService apiService;
    private static final String TAG = "ResumeRepository";

    public ResumeRepository() {
        apiService = RetrofitClient.getClient().create(ResumeApiService.class);
    }

    /**
     * UC29: Lấy danh sách resume của user hiện tại
     */
    public LiveData<ApiResponse<PaginationResponse<ResumeResponse>>> getMyResumes(int page, int size) {
        MutableLiveData<ApiResponse<PaginationResponse<ResumeResponse>>> data = new MutableLiveData<>();
        apiService.getMyResumes(page, size).enqueue(new Callback<ApiResponse<PaginationResponse<ResumeResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<PaginationResponse<ResumeResponse>>> call,
                                   Response<ApiResponse<PaginationResponse<ResumeResponse>>> response) {
                if (response.isSuccessful()) {
                    data.setValue(response.body());
                } else {
                    Log.e(TAG, "getMyResumes failed: " + response.code());
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<PaginationResponse<ResumeResponse>>> call, Throwable t) {
                Log.e(TAG, "getMyResumes error: " + t.getMessage());
                data.setValue(null);
            }
        });
        return data;
    }

    /**
     * Admin/HR: Lấy danh sách tất cả resume
     */
    public LiveData<ApiResponse<PaginationResponse<ResumeResponse>>> getResumes(int page, int size) {
        MutableLiveData<ApiResponse<PaginationResponse<ResumeResponse>>> data = new MutableLiveData<>();
        apiService.getResumes(page, size).enqueue(new Callback<ApiResponse<PaginationResponse<ResumeResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<PaginationResponse<ResumeResponse>>> call,
                                   Response<ApiResponse<PaginationResponse<ResumeResponse>>> response) {
                if (response.isSuccessful()) {
                    data.setValue(response.body());
                } else {
                    Log.e(TAG, "getResumes failed: " + response.code());
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<PaginationResponse<ResumeResponse>>> call, Throwable t) {
                Log.e(TAG, "getResumes error: " + t.getMessage());
                data.setValue(null);
            }
        });
        return data;
    }

    /**
     * UC24: Tạo mới resume (ứng tuyển công việc)
     */
    public LiveData<ApiResponse<ResumeResponse>> createResume(ResumeRequest request) {
        MutableLiveData<ApiResponse<ResumeResponse>> data = new MutableLiveData<>();
        apiService.createResume(request).enqueue(new Callback<ApiResponse<ResumeResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<ResumeResponse>> call,
                                   Response<ApiResponse<ResumeResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    data.setValue(response.body());
                } else {
                    Log.e(TAG, "createResume failed: " + response.code());
                    try {
                        if (response.errorBody() != null) {
                            Log.e(TAG, "Error: " + response.errorBody().string());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ResumeResponse>> call, Throwable t) {
                Log.e(TAG, "createResume error: " + t.getMessage());
                data.setValue(null);
            }
        });
        return data;
    }

    /**
     * UC24: Tải CV và ứng tuyển công việc sử dụng MultipartBody.Part
     */
    public LiveData<ApiResponse<ResumeResponse>> uploadResume(
            okhttp3.RequestBody email,
            okhttp3.RequestBody jobId,
            okhttp3.MultipartBody.Part file
    ) {
        MutableLiveData<ApiResponse<ResumeResponse>> data = new MutableLiveData<>();
        apiService.uploadResume(email, jobId, file).enqueue(new Callback<ApiResponse<ResumeResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<ResumeResponse>> call,
                                   Response<ApiResponse<ResumeResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    data.setValue(response.body());
                } else {
                    Log.e(TAG, "uploadResume failed: " + response.code());
                    try {
                        if (response.errorBody() != null) {
                            Log.e(TAG, "Error body: " + response.errorBody().string());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ResumeResponse>> call, Throwable t) {
                Log.e(TAG, "uploadResume error: " + t.getMessage());
                data.setValue(null);
            }
        });
        return data;
    }

    /**
     * UC27: Lấy chi tiết resume theo ID
     */
    public LiveData<ApiResponse<ResumeResponse>> getResumeById(int id) {
        MutableLiveData<ApiResponse<ResumeResponse>> data = new MutableLiveData<>();
        apiService.getResumeById(id).enqueue(new Callback<ApiResponse<ResumeResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<ResumeResponse>> call,
                                   Response<ApiResponse<ResumeResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    data.setValue(response.body());
                } else {
                    Log.e(TAG, "getResumeById failed: " + response.code());
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ResumeResponse>> call, Throwable t) {
                Log.e(TAG, "getResumeById error: " + t.getMessage());
                data.setValue(null);
            }
        });
        return data;
    }

    /**
     * UC26: Xóa resume
     */
    public LiveData<ApiResponse<Object>> deleteResume(int id) {
        MutableLiveData<ApiResponse<Object>> data = new MutableLiveData<>();
        apiService.deleteResume(id).enqueue(new Callback<ApiResponse<Object>>() {
            @Override
            public void onResponse(Call<ApiResponse<Object>> call,
                                   Response<ApiResponse<Object>> response) {
                if (response.isSuccessful()) {
                    data.setValue(response.body());
                } else {
                    Log.e(TAG, "deleteResume failed: " + response.code());
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                Log.e(TAG, "deleteResume error: " + t.getMessage());
                data.setValue(null);
            }
        });
        return data;
    }

    /**
     * UC25: Cập nhật nội dung resume (cho Candidate)
     */
    public LiveData<ApiResponse<ResumeResponse>> updateResume(int id, ResumeRequest request) {
        MutableLiveData<ApiResponse<ResumeResponse>> data = new MutableLiveData<>();
        apiService.updateResume(id, request).enqueue(new Callback<ApiResponse<ResumeResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<ResumeResponse>> call,
                                   Response<ApiResponse<ResumeResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    data.setValue(response.body());
                } else {
                    Log.e(TAG, "updateResume failed: " + response.code());
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ResumeResponse>> call, Throwable t) {
                Log.e(TAG, "updateResume error: " + t.getMessage());
                data.setValue(null);
            }
        });
        return data;
    }

    /**
     * UC25: Cập nhật trạng thái resume (cho HR/Admin)
     */
    public LiveData<ApiResponse<ResumeResponse>> updateResumeStatus(int id, String status) {
        MutableLiveData<ApiResponse<ResumeResponse>> data = new MutableLiveData<>();
        apiService.updateResumeStatus(id, new ResumeStatusRequest(status))
                .enqueue(new Callback<ApiResponse<ResumeResponse>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<ResumeResponse>> call,
                                           Response<ApiResponse<ResumeResponse>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            data.setValue(response.body());
                        } else {
                            data.setValue(null);
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<ResumeResponse>> call, Throwable t) {
                        Log.e(TAG, "updateResumeStatus error: " + t.getMessage());
                        data.setValue(null);
                    }
                });
        return data;
    }

    /**
     * Lấy danh sách CV nộp cho Job kèm điểm khớp kỹ năng, tự động sắp xếp điểm cao nhất lên đầu
     */
    public LiveData<ApiResponse<java.util.List<ResumeResponse>>> getMatchingCandidates(int jobId) {
        MutableLiveData<ApiResponse<java.util.List<ResumeResponse>>> data = new MutableLiveData<>();
        apiService.getMatchingCandidates(jobId).enqueue(new Callback<ApiResponse<java.util.List<ResumeResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<java.util.List<ResumeResponse>>> call,
                                   Response<ApiResponse<java.util.List<ResumeResponse>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    data.setValue(response.body());
                } else {
                    Log.e(TAG, "getMatchingCandidates failed: " + response.code());
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<java.util.List<ResumeResponse>>> call, Throwable t) {
                Log.e(TAG, "getMatchingCandidates error: " + t.getMessage());
                data.setValue(null);
            }
        });
        return data;
    }
}
