package com.example.timviecapp.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.timviecapp.models.common.ApiResponse;
import com.example.timviecapp.models.common.PaginationResponse;
import com.example.timviecapp.models.job.JobResponse;
import com.example.timviecapp.network.RetrofitClient;
import com.example.timviecapp.network.services.JobApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JobRepository {
    private final JobApiService jobApiService;

    public JobRepository() {
        jobApiService = RetrofitClient.getClient().create(JobApiService.class);
    }

    public LiveData<ApiResponse<PaginationResponse<JobResponse>>> getJobs(int page, int size) {
        MutableLiveData<ApiResponse<PaginationResponse<JobResponse>>> jobData = new MutableLiveData<>();
        
        jobApiService.getJobs(page, size, "createdAt", "desc", null, null).enqueue(new Callback<ApiResponse<PaginationResponse<JobResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<PaginationResponse<JobResponse>>> call, Response<ApiResponse<PaginationResponse<JobResponse>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    jobData.setValue(response.body());
                } else {
                    jobData.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<PaginationResponse<JobResponse>>> call, Throwable t) {
                jobData.setValue(null);
            }
        });
        
        return jobData;
    }

    public LiveData<ApiResponse<JobResponse>> getJobById(int id) {
        MutableLiveData<ApiResponse<JobResponse>> jobData = new MutableLiveData<>();
        jobApiService.getJobById(id).enqueue(new Callback<ApiResponse<JobResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<JobResponse>> call, Response<ApiResponse<JobResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    jobData.setValue(response.body());
                } else {
                    jobData.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<JobResponse>> call, Throwable t) {
                jobData.setValue(null);
            }
        });
        return jobData;
    }

    public LiveData<ApiResponse<JobResponse>> createJob(com.example.timviecapp.models.job.JobRequest request) {
        MutableLiveData<ApiResponse<JobResponse>> jobData = new MutableLiveData<>();
        jobApiService.createJob(request).enqueue(new Callback<ApiResponse<JobResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<JobResponse>> call, Response<ApiResponse<JobResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    jobData.setValue(response.body());
                } else {
                    jobData.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<JobResponse>> call, Throwable t) {
                jobData.setValue(null);
            }
        });
        return jobData;
    }

    public LiveData<ApiResponse<PaginationResponse<JobResponse>>> searchJobs(String keyword, String location, String level, int page, int size) {
        MutableLiveData<ApiResponse<PaginationResponse<JobResponse>>> jobData = new MutableLiveData<>();

        jobApiService.searchJobs(keyword, null, null, null, null, location, level, page, size).enqueue(new Callback<ApiResponse<PaginationResponse<JobResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<PaginationResponse<JobResponse>>> call, Response<ApiResponse<PaginationResponse<JobResponse>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    jobData.setValue(response.body());
                } else {
                    jobData.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<PaginationResponse<JobResponse>>> call, Throwable t) {
                jobData.setValue(null);
            }
        });

        return jobData;
    }

    public LiveData<ApiResponse<PaginationResponse<JobResponse>>> getJobsByCompany(int companyId, int page, int size) {
        MutableLiveData<ApiResponse<PaginationResponse<JobResponse>>> jobData = new MutableLiveData<>();
        
        jobApiService.getJobsByCompany(companyId, page, size).enqueue(new Callback<ApiResponse<PaginationResponse<JobResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<PaginationResponse<JobResponse>>> call, Response<ApiResponse<PaginationResponse<JobResponse>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    jobData.setValue(response.body());
                } else {
                    jobData.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<PaginationResponse<JobResponse>>> call, Throwable t) {
                jobData.setValue(null);
            }
        });
        
        return jobData;
    }

    public LiveData<ApiResponse<JobResponse>> updateJob(int id, com.example.timviecapp.models.job.JobRequest request) {
        MutableLiveData<ApiResponse<JobResponse>> jobData = new MutableLiveData<>();
        jobApiService.updateJob(id, request).enqueue(new Callback<ApiResponse<JobResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<JobResponse>> call, Response<ApiResponse<JobResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    jobData.setValue(response.body());
                } else {
                    jobData.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<JobResponse>> call, Throwable t) {
                jobData.setValue(null);
            }
        });
        return jobData;
    }
}
