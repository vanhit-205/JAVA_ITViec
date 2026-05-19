package com.example.timviecapp.utils;

import android.content.Context;
import com.example.timviecapp.storage.SharedPrefManager;

/**
 * TokenManager - Wrapper/Delegate for SharedPrefManager to maintain backward compatibility.
 * Delegates all calls to SharedPrefManager.
 */
public class TokenManager {
    private static SharedPrefManager sharedPrefManager;

    public static void init(Context context) {
        sharedPrefManager = SharedPrefManager.getInstance(context);
    }

    private static SharedPrefManager getManager() {
        if (sharedPrefManager == null) {
            throw new IllegalStateException("TokenManager is not initialized. Call init(Context) in Application.onCreate()");
        }
        return sharedPrefManager;
    }

    public static void saveToken(String accessToken) {
        getManager().saveToken(accessToken);
    }

    public static String getToken() {
        return getManager().getToken();
    }

    public static void saveRefreshToken(String refreshToken) {
        getManager().saveRefreshToken(refreshToken);
    }

    public static String getRefreshToken() {
        return getManager().getRefreshToken();
    }

    public static void saveUserInfo(int userId, String email, String name, String role) {
        getManager().saveUserInfo(userId, email, name, role);
    }

    public static int getUserId() {
        return getManager().getUserId();
    }

    public static String getUserEmail() {
        return getManager().getUserEmail();
    }

    public static String getUserName() {
        return getManager().getUserName();
    }

    public static String getUserRole() {
        return getManager().getUserRole();
    }

    public static boolean isLoggedIn() {
        return getManager().isLoggedIn();
    }

    public static void clear() {
        getManager().clear();
    }
}
