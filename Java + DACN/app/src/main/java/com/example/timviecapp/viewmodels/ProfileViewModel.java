package com.example.timviecapp.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.timviecapp.models.auth.UserResponse;
import com.example.timviecapp.models.common.ApiResponse;
import com.example.timviecapp.models.user.UpdateUserRequest;
import com.example.timviecapp.repository.UserRepository;

/**
 * ProfileViewModel - View Model cho trang thông tin cá nhân
 * UC4: Cập nhật thông tin cá nhân
 */
public class ProfileViewModel extends ViewModel {
    private final UserRepository userRepository;
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public ProfileViewModel() {
        userRepository = new UserRepository();
    }

    public LiveData<ApiResponse<UserResponse>> getUserById(int id) {
        isLoading.setValue(true);
        return userRepository.getUserById(id);
    }

    public LiveData<ApiResponse<UserResponse>> updateUser(int id, UpdateUserRequest request) {
        isLoading.setValue(true);
        return userRepository.updateUser(id, request);
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading.setValue(loading);
    }
}
