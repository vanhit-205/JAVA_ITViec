package com.example.timviecapp.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.timviecapp.models.common.ApiResponse;
import com.example.timviecapp.models.common.PaginationResponse;
import com.example.timviecapp.models.company.CompanyResponse;
import com.example.timviecapp.repository.CompanyRepository;

/**
 * CompanyViewModel - Xử lý logic cho màn hình công ty
 * UC9: Xem danh sách công ty
 * UC10: Xem chi tiết công ty
 */
public class CompanyViewModel extends ViewModel {
    private final CompanyRepository companyRepository;
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public CompanyViewModel() {
        companyRepository = new CompanyRepository();
    }

    /**
     * UC9: Lấy danh sách công ty
     */
    public LiveData<ApiResponse<PaginationResponse<CompanyResponse>>> getCompanies(int page, int size) {
        isLoading.setValue(true);
        return companyRepository.getCompanies(page, size);
    }

    /**
     * UC10: Lấy chi tiết công ty
     */
    public LiveData<ApiResponse<CompanyResponse>> getCompanyById(int id) {
        isLoading.setValue(true);
        return companyRepository.getCompanyById(id);
    }

    public LiveData<ApiResponse<CompanyResponse>> createCompany(com.example.timviecapp.models.company.CompanyRequest request) {
        isLoading.setValue(true);
        return companyRepository.createCompany(request);
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading.setValue(loading);
    }
}
