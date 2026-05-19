package com.example.domain.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_token_blacklist")
public class TokenBlacklist extends PanacheEntity {

    @Column(nullable = false, unique = true, columnDefinition = "TEXT")
    public String token;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    public TokenType tokenType;

    public String reason;

    @Column(nullable = false)
    public LocalDateTime blacklistedAt;

    @Column(nullable = false)
    public LocalDateTime expiredAt;

    public TokenBlacklist() {
        this.blacklistedAt = LocalDateTime.now();
    }

    public enum TokenType {
        ACCESS,
        REFRESH
    }
}
