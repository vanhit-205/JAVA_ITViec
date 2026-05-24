package com.example.timviecapp.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.timviecapp.models.common.ApiResponse;
import com.example.timviecapp.models.common.PaginationResponse;
import com.example.timviecapp.models.skill.SkillRequest;
import com.example.timviecapp.models.skill.SkillResponse;
import com.example.timviecapp.repository.SkillRepository;

public class SkillViewModel extends ViewModel {
    private final SkillRepository repository;
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public SkillViewModel() {
        repository = new SkillRepository();
    }

    public LiveData<ApiResponse<PaginationResponse<SkillResponse>>> getSkills(int page, int size) {
        isLoading.setValue(true);
        return repository.getSkills(page, size);
    }

    public LiveData<ApiResponse<SkillResponse>> createSkill(SkillRequest request) {
        isLoading.setValue(true);
        return repository.createSkill(request);
    }

    public LiveData<ApiResponse<SkillResponse>> updateSkill(int id, SkillRequest request) {
        isLoading.setValue(true);
        return repository.updateSkill(id, request);
    }

    public LiveData<ApiResponse<Object>> deleteSkill(int id) {
        isLoading.setValue(true);
        return repository.deleteSkill(id);
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading.setValue(loading);
    }
}
