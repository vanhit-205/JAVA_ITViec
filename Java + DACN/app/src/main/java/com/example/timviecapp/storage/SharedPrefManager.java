package com.example.timviecapp.storage;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.timviecapp.constants.Constants;

public class SharedPrefManager {
    private static SharedPrefManager instance;
    private final SharedPreferences sharedPreferences;

    private SharedPrefManager(Context context) {
        sharedPreferences = context.getApplicationContext().getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPrefManager(context);
        }
        return instance;
    }

    // Token Management
    public void saveToken(String token) {
        sharedPreferences.edit().putString(Constants.KEY_TOKEN, token).apply();
    }

    public String getToken() {
        return sharedPreferences.getString(Constants.KEY_TOKEN, null);
    }

    public void saveRefreshToken(String refreshToken) {
        sharedPreferences.edit().putString(Constants.KEY_REFRESH_TOKEN, refreshToken).apply();
    }

    public String getRefreshToken() {
        return sharedPreferences.getString(Constants.KEY_REFRESH_TOKEN, null);
    }

    // User Info Management
    public void saveUserInfo(int userId, String email, String name, String role) {
        sharedPreferences.edit()
                .putInt(Constants.KEY_USER_ID, userId)
                .putString(Constants.KEY_USER_EMAIL, email)
                .putString(Constants.KEY_USER_NAME, name)
                .putString(Constants.KEY_USER_ROLE, role)
                .apply();
    }

    public int getUserId() {
        return sharedPreferences.getInt(Constants.KEY_USER_ID, -1);
    }

    public String getUserEmail() {
        return sharedPreferences.getString(Constants.KEY_USER_EMAIL, null);
    }

    public String getUserName() {
        return sharedPreferences.getString(Constants.KEY_USER_NAME, null);
    }

    public String getUserRole() {
        return sharedPreferences.getString(Constants.KEY_USER_ROLE, null);
    }

    // Login State
    public boolean isLoggedIn() {
        String token = getToken();
        return token != null && !token.isEmpty();
    }

    // Clear All
    public void clear() {
        sharedPreferences.edit().clear().apply();
    }
}
