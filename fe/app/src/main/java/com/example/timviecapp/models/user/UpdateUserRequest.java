package com.example.timviecapp.models.user;

import com.google.gson.annotations.SerializedName;

public class UpdateUserRequest {
    @SerializedName("username")
    private String name;
    private String address;
    private Integer age;
    private String gender;
    private String role;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public void setAge(String ageStr) {
        if (ageStr == null || ageStr.trim().isEmpty()) {
            this.age = null;
        } else {
            try {
                this.age = Integer.parseInt(ageStr.trim());
            } catch (NumberFormatException e) {
                this.age = null;
            }
        }
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String genderStr) {
        if (genderStr == null || genderStr.trim().isEmpty()) {
            this.gender = null;
            return;
        }
        String clean = genderStr.trim().toUpperCase();
        if (clean.equals("NAM") || clean.equals("MALE")) {
            this.gender = "MALE";
        } else if (clean.equals("NỮ") || clean.equals("NU") || clean.equals("FEMALE")) {
            this.gender = "FEMALE";
        } else if (clean.equals("KHÁC") || clean.equals("KHAC") || clean.equals("OTHER")) {
            this.gender = "OTHER";
        } else {
            this.gender = clean; // Keep uppercase
        }
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
