package com.example.timviecapp.models.auth;

import com.google.gson.annotations.SerializedName;

public class RefreshResponse {
    @SerializedName("access_token")
    private String accessToken;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
