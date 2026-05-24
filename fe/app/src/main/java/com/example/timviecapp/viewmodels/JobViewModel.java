package com.example.timviecapp.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.timviecapp.models.common.ApiResponse;
import com.example.timviecapp.models.common.PaginationResponse;
import com.example.timviecapp.models.job.JobResponse;
import com.example.timviecapp.repository.JobRepository;

/**
 * JobViewModel - Xử lý logic cho danh sách và chi tiết công việc
 * UC14: Xem danh sách công việc
 * UC15: Xem chi tiết công việc
 * UC16: Tìm kiếm công việc theo kỹ năng
 */
public class JobViewModel extends ViewModel {
    private final JobRepository jobRepository;
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public JobViewModel() {
        jobRepository = new JobRepository();
    }

    /**
     * UC14: Lấy danh sách công việc
     */
    public LiveData<ApiResponse<PaginationResponse<JobResponse>>> getJobs(int page, int size) {
        isLoading.setValue(true);
        return jobRepository.getJobs(page, size);
    }

    /**
     * UC15: Lấy chi tiết công việc theo ID
     */
    public LiveData<ApiResponse<JobResponse>> getJobById(int id) {
        isLoading.setValue(true);
        return jobRepository.getJobById(id);
    }

    /**
     * UC: Thêm công việc mới
     */
    public LiveData<ApiResponse<JobResponse>> createJob(com.example.timviecapp.models.job.JobRequest request) {
        isLoading.setValue(true);
        return jobRepository.createJob(request);
    }

    /**
     * UC16: Tìm kiếm công việc theo từ khóa kỹ năng
     */
    public LiveData<ApiResponse<PaginationResponse<JobResponse>>> searchJobs(
            String keyword, String location, String level, int page, int size) {
        isLoading.setValue(true);
        return jobRepository.searchJobs(keyword, location, level, page, size);
    }

    /**
     * Lấy danh sách công việc của một công ty
     */
    public LiveData<ApiResponse<PaginationResponse<JobResponse>>> getJobsByCompany(int companyId, int page, int size) {
        isLoading.setValue(true);
        return jobRepository.getJobsByCompany(companyId, page, size);
    }

    /**
     * UC: Cập nhật thông tin công việc
     */
    public LiveData<ApiResponse<JobResponse>> updateJob(int id, com.example.timviecapp.models.job.JobRequest request) {
        isLoading.setValue(true);
        return jobRepository.updateJob(id, request);
    }

    public LiveData<ApiResponse<Object>> deleteJob(int id) {
        isLoading.setValue(true);
        return jobRepository.deleteJob(id);
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading.setValue(loading);
    }
}
