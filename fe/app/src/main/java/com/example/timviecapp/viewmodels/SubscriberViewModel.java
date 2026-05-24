package com.example.timviecapp.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.timviecapp.models.common.ApiResponse;
import com.example.timviecapp.models.subscriber.SubscriberRequest;
import com.example.timviecapp.models.subscriber.SubscriberResponse;
import com.example.timviecapp.repository.SubscriberRepository;

/**
 * SubscriberViewModel - Xử lý logic đăng ký nhận thông báo
 * UC30: Tạo mới Subscriber
 * UC31: Cập nhật Subscriber
 */
public class SubscriberViewModel extends ViewModel {
    private final SubscriberRepository repository;
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public SubscriberViewModel() {
        repository = new SubscriberRepository();
    }

    /**
     * UC30: Đăng ký nhận thông báo việc làm
     */
    public LiveData<ApiResponse<SubscriberResponse>> createSubscriber(SubscriberRequest request) {
        isLoading.setValue(true);
        return repository.createSubscriber(request);
    }

    /**
     * UC31: Cập nhật thông tin subscriber
     */
    public LiveData<ApiResponse<SubscriberResponse>> updateSubscriber(int id, SubscriberRequest request) {
        isLoading.setValue(true);
        return repository.updateSubscriber(id, request);
    }

    /**
     * Hủy đăng ký nhận thông báo
     */
    public LiveData<ApiResponse<SubscriberResponse>> disableSubscriber(int id) {
        isLoading.setValue(true);
        return repository.disableSubscriber(id);
    }

    /**
     * Lấy thông tin subscriber
     */
    public LiveData<ApiResponse<SubscriberResponse>> getSubscriber(int id) {
        isLoading.setValue(true);
        return repository.getSubscriber(id);
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading.setValue(loading);
    }
}
