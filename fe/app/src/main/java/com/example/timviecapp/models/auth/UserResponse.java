package com.example.timviecapp.models.auth;

import com.google.gson.annotations.SerializedName;

public class UserResponse {
    private int id;
    private String email;
    
    @SerializedName("username")
    private String name;
    
    private String role;
    private String age;
    private String gender;
    private String address;

    @SerializedName("accountNonLocked")
    private Boolean accountNonLocked;

    @SerializedName("enabled")
    private Boolean isEnabled;

    public int getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getRole() {
        return role;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Boolean isLocked() {
        return accountNonLocked != null ? !accountNonLocked : false;
    }

    public void setLocked(Boolean locked) {
        this.accountNonLocked = locked != null ? !locked : true;
    }

    public Boolean isEnabled() {
        return isEnabled != null ? isEnabled : true;
    }

    public void setEnabled(Boolean enabled) {
        isEnabled = enabled;
    }
}
