package com.example.timviecapp;

import android.app.Application;

import com.example.timviecapp.utils.TokenManager;

/**
 * Application class - Khởi tạo các thành phần toàn cục
 */
public class WorkHubApp extends Application {
    private static WorkHubApp instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        // Khởi tạo TokenManager với context
        TokenManager.init(this);
    }

    public static WorkHubApp getInstance() {
        return instance;
    }
}
