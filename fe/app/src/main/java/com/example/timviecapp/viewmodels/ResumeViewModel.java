package com.example.timviecapp.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.timviecapp.models.common.ApiResponse;
import com.example.timviecapp.models.common.PaginationResponse;
import com.example.timviecapp.models.resume.ResumeRequest;
import com.example.timviecapp.models.resume.ResumeResponse;
import com.example.timviecapp.repository.ResumeRepository;

/**
 * ResumeViewModel - Xử lý logic cho hồ sơ ứng tuyển
 * UC24: Tạo mới resume
 * UC26: Xóa resume
 * UC27: Xem chi tiết resume
 * UC29: Lấy danh sách resume theo user
 */
public class ResumeViewModel extends ViewModel {
    private final ResumeRepository resumeRepository;
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public ResumeViewModel() {
        resumeRepository = new ResumeRepository();
    }

    /**
     * UC29: Lấy danh sách resume của user
     */
    public LiveData<ApiResponse<PaginationResponse<ResumeResponse>>> getMyResumes(int page, int size) {
        isLoading.setValue(true);
        return resumeRepository.getMyResumes(page, size);
    }

    /**
     * Admin/HR: Lấy danh sách tất cả resume
     */
    public LiveData<ApiResponse<PaginationResponse<ResumeResponse>>> getResumes(int page, int size) {
        isLoading.setValue(true);
        return resumeRepository.getResumes(page, size);
    }

    /**
     * UC24: Tạo resume mới (ứng tuyển)
     */
    public LiveData<ApiResponse<ResumeResponse>> createResume(ResumeRequest request) {
        isLoading.setValue(true);
        return resumeRepository.createResume(request);
    }

    /**
     * UC24: Tải CV và ứng tuyển sử dụng Multipart
     */
    public LiveData<ApiResponse<ResumeResponse>> uploadResume(
            okhttp3.RequestBody email,
            okhttp3.RequestBody jobId,
            okhttp3.MultipartBody.Part file
    ) {
        isLoading.setValue(true);
        return resumeRepository.uploadResume(email, jobId, file);
    }

    /**
     * UC27: Xem chi tiết resume
     */
    public LiveData<ApiResponse<ResumeResponse>> getResumeById(int id) {
        isLoading.setValue(true);
        return resumeRepository.getResumeById(id);
    }

    /**
     * UC26: Xóa resume
     */
    public LiveData<ApiResponse<Object>> deleteResume(int id) {
        isLoading.setValue(true);
        return resumeRepository.deleteResume(id);
    }

    /**
     * UC25: Cập nhật nội dung resume
     */
    public LiveData<ApiResponse<ResumeResponse>> updateResume(int id, ResumeRequest request) {
        isLoading.setValue(true);
        return resumeRepository.updateResume(id, request);
    }

    /**
     * UC25: Cập nhật trạng thái resume (Duyệt hồ sơ)
     */
    public LiveData<ApiResponse<ResumeResponse>> updateResumeStatus(int id, String status) {
        isLoading.setValue(true);
        return resumeRepository.updateResumeStatus(id, status);
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading.setValue(loading);
    }

    /**
     * Lấy danh sách CV nộp cho Job kèm điểm khớp kỹ năng
     */
    public LiveData<ApiResponse<java.util.List<ResumeResponse>>> getMatchingCandidates(int jobId) {
        isLoading.setValue(true);
        return resumeRepository.getMatchingCandidates(jobId);
    }
}
