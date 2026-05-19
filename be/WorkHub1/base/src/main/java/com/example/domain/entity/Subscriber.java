package com.example.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "tbl_subscribers", indexes = {
    @Index(name = "idx_subscriber_email", columnList = "email"),
    @Index(name = "idx_subscriber_deleted", columnList = "deleted"),
    @Index(name = "idx_subscriber_enabled", columnList = "enabled"),
    @Index(name = "idx_subscriber_createdAt", columnList = "createdAt"),
    @Index(name = "idx_subscriber_email_deleted_enabled", columnList = "email, deleted, enabled")
})
public class Subscriber extends PanacheEntity {

    public String name;

    public String email;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "tbl_subscriber_skill",
            joinColumns = @JoinColumn(name = "subscriber_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    public List<Skill> skills;

    // Status
    public boolean enabled = true;

    // Soft delete
    public boolean deleted = false;

    // Auditing
    public Instant createdAt;
    public Instant updatedAt;
    public Long createdBy;
    public Long updatedBy;

    // Email tracking
    public Instant lastSentAt;
    public int emailSentCount = 0;

    @PrePersist
    public void prePersist() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
        if (!enabled) {
            enabled = true;
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

    public void enable() {
        this.enabled = true;
    }

    public void disable() {
        this.enabled = false;
    }

    public void updateLastSent() {
        this.lastSentAt = Instant.now();
        this.emailSentCount++;
    }

    public boolean canSendEmail() {
        return enabled && !deleted;
    }
}