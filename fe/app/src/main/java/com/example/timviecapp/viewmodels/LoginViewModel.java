package com.example.timviecapp.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.timviecapp.models.auth.LoginResponse;
import com.example.timviecapp.models.common.ApiResponse;
import com.example.timviecapp.repository.AuthRepository;

public class LoginViewModel extends ViewModel {
    private final AuthRepository authRepository;
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public LoginViewModel() {
        authRepository = new AuthRepository();
    }

    public LiveData<ApiResponse<LoginResponse>> login(String email, String password) {
        isLoading.setValue(true);
        LiveData<ApiResponse<LoginResponse>> response = authRepository.login(email, password);
        // Note: In a real app, you might want to transform this or observe it here to set isLoading to false
        return response;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading.setValue(loading);
    }
}
