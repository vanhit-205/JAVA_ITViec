package com.example.repository;

import com.example.domain.entity.UserSession;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class UserSessionRepository implements PanacheRepository<UserSession> {

    public List<UserSession> findActiveByUserId(Long userId) {
        return list("user.id = ?1 and isActive = true", userId);
    }

    public Optional<UserSession> findActiveByAccessToken(String accessToken) {
        return find("accessToken = ?1 and isActive = true", accessToken).firstResultOptional();
    }

    public Optional<UserSession> findActiveByRefreshToken(String refreshToken) {
        return find("refreshToken = ?1 and isActive = true", refreshToken).firstResultOptional();
    }

    public void deactivateAllByUserId(Long userId) {
        update("isActive = false where user.id = ?1 and isActive = true", userId);
    }

    public void deactivateByAccessToken(String accessToken) {
        update("isActive = false where accessToken = ?1", accessToken);
    }
}
