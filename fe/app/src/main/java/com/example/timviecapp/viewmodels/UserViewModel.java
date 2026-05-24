package com.example.timviecapp.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.timviecapp.models.auth.UserResponse;
import com.example.timviecapp.models.common.ApiResponse;
import com.example.timviecapp.models.common.PaginationResponse;
import com.example.timviecapp.models.user.UpdateUserRequest;
import com.example.timviecapp.repository.UserRepository;

public class UserViewModel extends ViewModel {
    private final UserRepository repository;
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public UserViewModel() {
        repository = new UserRepository();
    }

    public LiveData<ApiResponse<PaginationResponse<UserResponse>>> getUsers(int page, int size, String keyword) {
        isLoading.setValue(true);
        return repository.getUsers(page, size, keyword);
    }

    public LiveData<ApiResponse<UserResponse>> getUserById(int id) {
        isLoading.setValue(true);
        return repository.getUserById(id);
    }

    public LiveData<ApiResponse<UserResponse>> updateUser(int id, UpdateUserRequest request) {
        isLoading.setValue(true);
        return repository.updateUser(id, request);
    }

    public LiveData<ApiResponse<UserResponse>> lockUser(int id) {
        isLoading.setValue(true);
        return repository.lockUser(id);
    }

    public LiveData<ApiResponse<UserResponse>> unlockUser(int id) {
        isLoading.setValue(true);
        return repository.unlockUser(id);
    }

    public LiveData<ApiResponse<UserResponse>> disableUser(int id) {
        isLoading.setValue(true);
        return repository.disableUser(id);
    }

    public LiveData<ApiResponse<UserResponse>> enableUser(int id) {
        isLoading.setValue(true);
        return repository.enableUser(id);
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading.setValue(loading);
    }
}
