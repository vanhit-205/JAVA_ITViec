package com.example.domain.entity;


import com.example.constant.StatusEnum;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "tbl_resumes", indexes = {
    @Index(name = "idx_resume_user", columnList = "user_id"),
    @Index(name = "idx_resume_job", columnList = "job_id"),
    @Index(name = "idx_resume_status", columnList = "status"),
    @Index(name = "idx_resume_deleted", columnList = "deleted"),
    @Index(name = "idx_resume_user_job_deleted", columnList = "user_id, job_id, deleted")
})
public class Resume extends PanacheEntity {

    public String email;

    public String url;

    @Enumerated(EnumType.STRING)
    public StatusEnum status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    public User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id")
    public Job job;

    // Soft delete
    public boolean deleted = false;

    // Auditing
    public Instant createdAt;
    public Instant updatedAt;
    public Long createdBy;
    public Long updatedBy;

    @PrePersist
    public void prePersist() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
        if (status == null) {
            status = StatusEnum.PENDING;
        }
        if (!deleted) {
            deleted = false;
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = Instant.now();
    }

    // Business methods
    public void softDelete() {
        this.deleted = true;
    }

    public void restore() {
        this.deleted = false;
    }

    public void approve() {
        if (this.status != StatusEnum.REVIEWING) {
            throw new IllegalStateException("Can only approve resumes in REVIEWING status");
        }
        this.status = StatusEnum.APPROVED;
    }

    public void reject() {
        if (this.status != StatusEnum.REVIEWING) {
            throw new IllegalStateException("Can only reject resumes in REVIEWING status");
        }
        this.status = StatusEnum.REJECTED;
    }

    public void startReview() {
        if (this.status != StatusEnum.PENDING) {
            throw new IllegalStateException("Can only start review for resumes in PENDING status");
        }
        this.status = StatusEnum.REVIEWING;
    }
}