package com.example.timviecapp.models.user;

public class RejectPayload {
    private String adminNotes;

    public RejectPayload(String adminNotes) {
        this.adminNotes = adminNotes;
    }

    public String getAdminNotes() {
        return adminNotes;
    }

    public void setAdminNotes(String adminNotes) {
        this.adminNotes = adminNotes;
    }
}
