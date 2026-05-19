package com.example.domain.dto.response;

public class LoginResponse {

    public String accessToken;
    public String refreshToken;
    public String tokenType = "Bearer";
    public long expiresIn;
    public UserResponse user;

    public LoginResponse() {}

    public LoginResponse(String accessToken, String refreshToken, long expiresIn, UserResponse user) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
        this.user = user;
    }
}
