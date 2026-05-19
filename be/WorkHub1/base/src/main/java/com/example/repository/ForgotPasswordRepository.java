package com.example.repository;

import com.example.domain.entity.ForgotPassword;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDateTime;
import java.util.Optional;

@ApplicationScoped
public class ForgotPasswordRepository implements PanacheRepository<ForgotPassword> {

    /**
     * Find the most recent unverified OTP for a user
     */
    public Optional<ForgotPassword> findValidOtp(Long userId) {
        return find("user.id = ?1 and verified = false and used = false and expirationTime > ?2",
                userId, LocalDateTime.now())
                .firstResultOptional();
    }

    /**
     * Find the most recent unverified OTP for a user by email
     */
    public Optional<ForgotPassword> findValidOtpByEmail(String email) {
        return find("user.email = ?1 and verified = false and used = false and expirationTime > ?2",
                email, LocalDateTime.now())
                .firstResultOptional();
    }

    /**
     * Check if there's a recent OTP request (for rate limiting)
     * Returns true if there's an OTP sent within the cooldown period
     */
    public boolean existsRecentOtp(Long userId, LocalDateTime since) {
        return count("user.id = ?1 and createdAt > ?2", userId, since) > 0;
    }

    /**
     * Delete expired OTP records
     */
    public long deleteExpired() {
        return delete("expirationTime < ?1", LocalDateTime.now());
    }

    /**
     * Delete all OTP records for a user (after successful password change)
     */
    public long deleteAllForUser(Long userId) {
        return delete("user.id", userId);
    }

    /**
     * Find OTP by ID and user ID
     */
    public Optional<ForgotPassword> findByIdAndUserId(Long id, Long userId) {
        return find("id = ?1 and user.id = ?2", id, userId).firstResultOptional();
    }
}
