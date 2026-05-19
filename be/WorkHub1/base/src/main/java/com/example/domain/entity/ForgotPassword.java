package com.example.domain.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Entity for managing OTP-based password reset.
 *
 * WHY ManyToOne instead of OneToOne?
 * - ManyToOne allows multiple OTP requests per user over time
 * - Easy to query all OTP records for a user (audit trail)
 * - OneToOne would prevent tracking historical OTP requests
 * - Better for rate limiting - we can check all recent requests
 */
@Entity
@Table(name = "tbl_forgot_password")
public class ForgotPassword extends PanacheEntity {

    /**
     * Hashed OTP - NEVER store plain OTP in production!
     * Security risk of raw OTP:
     * - If DB is compromised, attackers can use OTP immediately
     * - OTP in logs/traffic can be intercepted
     * - Hashing adds a layer of protection even if DB is leaked
     */
    @Column(nullable = false)
    public String hashedOtp;

    @Column(nullable = false)
    public LocalDateTime expirationTime;

    @Column(nullable = false)
    public LocalDateTime createdAt;

    /**
     * OTP can only be used once
     */
    @Column(nullable = false)
    public boolean verified = false;

    /**
     * Track if password was successfully changed
     */
    @Column(nullable = false)
    public boolean used = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    public User user;

    /**
     * IP address for security tracking
     */
    @Column
    public String ipAddress;

    public ForgotPassword() {
        this.createdAt = LocalDateTime.now();
    }

    /**
     * Check if OTP is expired
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expirationTime);
    }

    /**
     * Check if OTP can still be used
     */
    public boolean isUsable() {
        return !verified && !used && !isExpired();
    }
}
