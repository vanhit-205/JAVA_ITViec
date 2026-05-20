package com.example.timviecapp.models.auth;

import com.google.gson.annotations.SerializedName;

public class ChangePasswordRequest {
    @SerializedName("email")
    private String email;

    @SerializedName("otp")
    private String otp;

    @SerializedName("newPassword")
    private String newPassword;

    @SerializedName("repeatPassword")
    private String repeatPassword;

    public ChangePasswordRequest(String email, String otp, String newPassword, String repeatPassword) {
        this.email = email;
        this.otp = otp;
        this.newPassword = newPassword;
        this.repeatPassword = repeatPassword;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getRepeatPassword() {
        return repeatPassword;
    }

    public void setRepeatPassword(String repeatPassword) {
        this.repeatPassword = repeatPassword;
    }
}
