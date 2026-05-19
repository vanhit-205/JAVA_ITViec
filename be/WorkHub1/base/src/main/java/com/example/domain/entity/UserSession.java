package com.example.domain.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_user_sessions")
public class UserSession extends PanacheEntity {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    public User user;

    @Column(nullable = false, columnDefinition = "TEXT")
    public String accessToken;

    @Column(nullable = false , columnDefinition = "TEXT")
    public String refreshToken;

    @Column(nullable = false)
    public String ipAddress;

    @Column(nullable = false)
    public String userAgent;

    @Column(nullable = false)
    public LocalDateTime createdAt;

    @Column(nullable = false)
    public LocalDateTime expiresAt;

    @Column(nullable = false)
    public boolean isActive = true;

    public UserSession() {
        this.createdAt = LocalDateTime.now();
    }
}
