package com.example.timviecapp.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.timviecapp.models.common.ApiResponse;
import com.example.timviecapp.repository.ForgotPasswordRepository;

public class ForgotPasswordViewModel extends ViewModel {
    private final ForgotPasswordRepository repository;
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public ForgotPasswordViewModel() {
        repository = new ForgotPasswordRepository();
    }

    public LiveData<ApiResponse<Object>> verifyEmail(String email) {
        isLoading.setValue(true);
        return repository.verifyEmail(email);
    }

    public LiveData<ApiResponse<Object>> verifyOtp(String email, String otp) {
        isLoading.setValue(true);
        return repository.verifyOtp(email, otp);
    }

    public LiveData<ApiResponse<Object>> changePassword(String email, String otp, String newPassword) {
        isLoading.setValue(true);
        return repository.changePassword(email, otp, newPassword);
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading.setValue(loading);
    }
}
