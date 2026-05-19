package com.example.timviecapp.models.user;

public class UpgradeRequestPayload {
    private Long companyId;
    private String newCompanyName;
    private String reason;

    public UpgradeRequestPayload(Long companyId, String newCompanyName, String reason) {
        this.companyId = companyId;
        this.newCompanyName = newCompanyName;
        this.reason = reason;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getNewCompanyName() {
        return newCompanyName;
    }

    public void setNewCompanyName(String newCompanyName) {
        this.newCompanyName = newCompanyName;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
