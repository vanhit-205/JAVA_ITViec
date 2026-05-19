package com.example.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_role_upgrade_requests")
public class RoleUpgradeRequest extends PanacheEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    public User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    @JsonIgnore
    public Company company; // Nếu muốn tham gia vào công ty sẵn có

    @Column(name = "applicant_name")
    public String applicantName;

    @Column(name = "applicant_email")
    public String applicantEmail;

    @Column(name = "new_company_name")
    public String newCompanyName; // Nếu muốn tạo công ty mới

    @Column(name = "new_company_website")
    public String newCompanyWebsite;

    @Column(name = "status", nullable = false, length = 20)
    public String status = "PENDING"; // PENDING, APPROVED, REJECTED

    @Column(name = "reason", columnDefinition = "TEXT")
    public String reason; // Lý do muốn nâng cấp tài khoản

    @Column(name = "admin_notes", columnDefinition = "TEXT")
    public String adminNotes; // Ghi chú từ admin khi duyệt/từ chối

    @Column(name = "created_at", nullable = false)
    public LocalDateTime createdAt;

    @Column(name = "updated_at")
    public LocalDateTime updatedAt;

    @Column(name = "approved_by")
    public Long approvedBy; // ID của Admin đã xử lý yêu cầu

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.status = "PENDING";
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
