package com.example.domain.dto.request;

import com.example.constant.StatusEnum;
import jakarta.validation.constraints.NotNull;

public class ResumeStatusUpdateRequest {

    @NotNull(message = "Status khong duoc de trong")
    public StatusEnum status;

    public ResumeStatusUpdateRequest() {
    }

    public ResumeStatusUpdateRequest(StatusEnum status) {
        this.status = status;
    }

    public StatusEnum getStatus() {
        return status;
    }

    public void setStatus(StatusEnum status) {
        this.status = status;
    }
}
