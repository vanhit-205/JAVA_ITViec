package com.example.service;

import com.example.domain.entity.TokenBlacklist;
import com.example.repository.TokenBlacklistRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.time.LocalDateTime;

@ApplicationScoped
public class TokenBlacklistService {

    private static final Logger log = Logger.getLogger(TokenBlacklistService.class);

    @Inject
    TokenBlacklistRepository tokenBlacklistRepository;

    public boolean isBlacklisted(String token) {
        return tokenBlacklistRepository.isBlacklisted(token);
    }

    @Transactional
    public void blacklistAccessToken(String token, String reason, java.time.Instant expiredAt) {
        if (token == null || token.isEmpty()) return;

        LocalDateTime expiry = LocalDateTime.ofInstant(expiredAt, java.time.ZoneId.systemDefault());
        tokenBlacklistRepository.blacklistToken(token, TokenBlacklist.TokenType.ACCESS, reason, expiry);
        log.info("Blacklisted access token: " + reason);
    }

    @Transactional
    public void blacklistRefreshToken(String token, String reason, java.time.Instant expiredAt) {
        if (token == null || token.isEmpty()) return;

        LocalDateTime expiry = LocalDateTime.ofInstant(expiredAt, java.time.ZoneId.systemDefault());
        tokenBlacklistRepository.blacklistToken(token, TokenBlacklist.TokenType.REFRESH, reason, expiry);
        log.info("Blacklisted refresh token: " + reason);
    }

    @Transactional
    public void cleanupExpiredEntries() {
        long deleted = tokenBlacklistRepository.delete("expiredAt < ?1", LocalDateTime.now());
        log.info("Cleaned up " + deleted + " expired blacklist entries");
    }
}
