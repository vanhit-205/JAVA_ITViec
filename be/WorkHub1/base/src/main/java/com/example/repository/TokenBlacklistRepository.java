package com.example.repository;

import com.example.domain.entity.TokenBlacklist;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TokenBlacklistRepository implements PanacheRepository<TokenBlacklist> {

    public boolean isBlacklisted(String token) {
        return count("token", token) > 0;
    }

    public void blacklistToken(String token, TokenBlacklist.TokenType tokenType, String reason, java.time.LocalDateTime expiredAt) {
        TokenBlacklist blacklist = new TokenBlacklist();
        blacklist.token = token;
        blacklist.tokenType = tokenType;
        blacklist.reason = reason;
        blacklist.expiredAt = expiredAt;
        persist(blacklist);
    }
}
