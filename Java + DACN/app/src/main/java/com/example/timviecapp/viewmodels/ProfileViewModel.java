package com.example.timviecapp.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.timviecapp.models.auth.UserResponse;
import com.example.timviecapp.models.common.ApiResponse;
import com.example.timviecapp.models.user.RoleUpgradeRequest;
import com.example.timviecapp.models.user.UpdateUserRequest;
import com.example.timviecapp.repository.UserRepository;
import com.example.timviecapp.repository.RoleUpgradeRepository;

/**
 * ProfileViewModel - View Model cho trang thông tin cá nhân
 * UC4: Cập nhật thông tin cá nhân
 */
public class ProfileViewModel extends ViewModel {
    private final UserRepository userRepository;
    private final RoleUpgradeRepository roleUpgradeRepository;
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public ProfileViewModel() {
        userRepository = new UserRepository();
        roleUpgradeRepository = new RoleUpgradeRepository();
    }

    public LiveData<ApiResponse<UserResponse>> getUserById(int id) {
        isLoading.setValue(true);
        return userRepository.getUserById(id);
    }

    public LiveData<ApiResponse<UserResponse>> updateUser(int id, UpdateUserRequest request) {
        isLoading.setValue(true);
        return userRepository.updateUser(id, request);
    }

    public LiveData<ApiResponse<RoleUpgradeRequest>> requestUpgrade(String newCompanyName, String reason) {
        isLoading.setValue(true);
        return roleUpgradeRepository.requestUpgrade(newCompanyName, reason);
    }

    public LiveData<ApiResponse<RoleUpgradeRequest>> getMyRequest() {
        isLoading.setValue(true);
        return roleUpgradeRepository.getMyRequest();
    }

    public LiveData<ApiResponse<java.util.List<RoleUpgradeRequest>>> getAllRequests() {
        isLoading.setValue(true);
        return roleUpgradeRepository.getAllRequests();
    }

    public LiveData<ApiResponse<Object>> approveRequest(int id) {
        isLoading.setValue(true);
        return roleUpgradeRepository.approveRequest(id);
    }

    public LiveData<ApiResponse<Object>> rejectRequest(int id, String adminNotes) {
        isLoading.setValue(true);
        return roleUpgradeRepository.rejectRequest(id, adminNotes);
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading.setValue(loading);
    }
}
