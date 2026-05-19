package com.example.timviecapp.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.timviecapp.models.auth.UserResponse;
import com.example.timviecapp.models.common.ApiResponse;
import com.example.timviecapp.repository.AuthRepository;

public class RegisterViewModel extends ViewModel {
    private final AuthRepository authRepository;
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public RegisterViewModel() {
        authRepository = new AuthRepository();
    }

    public LiveData<ApiResponse<UserResponse>> register(String email, String password, String name, String role) {
        isLoading.setValue(true);
        return authRepository.register(email, password, name, role);
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading.setValue(loading);
    }
}
