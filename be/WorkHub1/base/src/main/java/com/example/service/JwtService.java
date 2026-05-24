package com.example.service;

import com.example.domain.entity.User;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@ApplicationScoped
public class JwtService {

    private static final Logger log = Logger.getLogger(JwtService.class);

    @ConfigProperty(name = "mp.jwt.verify.publickey.location", defaultValue = "publicKey.pem")
    String publicKeyLocation;

    @ConfigProperty(name = "mp.jwt.verify.issuer", defaultValue = "https://example.com")
    String issuer;

    // Access token expires in 15 minutes
    private static final Duration ACCESS_TOKEN_EXPIRY = Duration.ofMinutes(15);

    // Refresh token expires in 7 days
    private static final Duration REFRESH_TOKEN_EXPIRY = Duration.ofDays(7);

    /**
     * Generate access token for user
     */
    public String generateAccessToken(User user) {
        Set<String> groups = new HashSet<>();
        groups.add(user.role.name);

        Instant now = Instant.now();
        Instant expiry = now.plus(ACCESS_TOKEN_EXPIRY);

        var jwtBuilder = Jwt.issuer(issuer)
                .subject(user.id.toString())
                .upn(user.email)
                .claim("userId", user.id)
                .claim("email", user.email)
                .claim("username", user.username)
                .claim("role", user.role.name)
                .groups(groups)
                .issuedAt(now)
                .expiresAt(expiry);

        // Nhúng companyId vào JWT nếu user đang thuộc một công ty (dùng cho authorization RECRUITER)
        if (user.company != null && user.company.id != null) {
            jwtBuilder = jwtBuilder.claim("companyId", user.company.id);
        }

        String token = jwtBuilder.sign();
        log.info("Generated access token for user: " + user.email);
        return token;
    }

    /**
     * Generate refresh token for user
     */
    public String generateRefreshToken(User user) {
        Instant now = Instant.now();
        Instant expiry = now.plus(REFRESH_TOKEN_EXPIRY);

        String token = Jwt.issuer(issuer)
                .subject(user.id.toString())
                .claim("type", "refresh")
                .claim("userId", user.id)
                .claim("email", user.email)
                .issuedAt(now)
                .expiresAt(expiry)
                .sign();

        log.info("Generated refresh token for user: " + user.email);
        return token;
    }

    /**
     * Get access token expiry in seconds
     */
    public long getAccessTokenExpiry() {
        return ACCESS_TOKEN_EXPIRY.getSeconds();
    }

    /**
     * Get refresh token expiry in seconds
     */
    public long getRefreshTokenExpiry() {
        return REFRESH_TOKEN_EXPIRY.getSeconds();
    }
}
