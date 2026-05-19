package com.example.service;

import com.example.constant.ErrorCode;
import com.example.constant.RoleConstant;
import com.example.domain.dto.request.LoginRequest;
import com.example.domain.dto.request.LogoutRequest;
import com.example.domain.dto.request.RefreshTokenRequest;
import com.example.domain.dto.request.RegisterRequest;
import com.example.domain.dto.response.LoginResponse;
import com.example.domain.dto.response.TokenResponse;
import com.example.domain.dto.response.UserResponse;
import com.example.domain.entity.User;
import com.example.domain.entity.UserSession;
import com.example.exception.AppException;
import com.example.mapper.UserMapper;
import com.example.repository.RoleRepository;
import com.example.repository.UserRepository;
import com.example.repository.UserSessionRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;
import org.mindrot.jbcrypt.BCrypt;

import java.time.LocalDateTime;

@ApplicationScoped
public class AuthService {

    private static final Logger log = Logger.getLogger(AuthService.class);

    @Inject
    UserRepository userRepository;

    @Inject
    RoleRepository roleRepository;

    @Inject
    UserSessionRepository userSessionRepository;

    @Inject
    JwtService jwtService;

    @Inject
    TokenBlacklistService tokenBlacklistService;

    @Inject
    UserMapper userMapper;

    /**
     * Register new user
     */
    @Transactional
    public UserResponse register(RegisterRequest request) {
        log.info("Registering user: " + request.email);

        // Check if username exists
        if (userRepository.existsByUsername(request.username)) {
            throw new AppException(ErrorCode.USERNAME_EXISTS.code, ErrorCode.USERNAME_EXISTS.message);
        }

        // Check if email exists
        if (userRepository.existsByEmail(request.email)) {
            throw new AppException(ErrorCode.EMAIL_EXISTS.code, ErrorCode.EMAIL_EXISTS.message);
        }

        // Determine role (default to ROLE_CANDIDATE if not specified)
        String roleName = request.role;
        if (roleName == null || roleName.isEmpty()) {
            roleName = RoleConstant.ROLE_CANDIDATE.name();
        } else {
            if (!roleName.startsWith("ROLE_")) {
                roleName = "ROLE_" + roleName.toUpperCase();
            }
        }

        final String finalRoleName = roleName;
        var role = roleRepository.findByName(finalRoleName)
                .orElseGet(() -> roleRepository.findByName(RoleConstant.ROLE_CANDIDATE.name())
                        .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND.code, ErrorCode.ROLE_NOT_FOUND.message)));

        // Create user
        User user = new User();
        user.username = request.username;
        user.email = request.email;
        user.password = BCrypt.hashpw(request.password, BCrypt.gensalt());
        user.role = role;
        user.enabled = true;
        user.accountNonLocked = true;

        userRepository.persist(user);
        log.info("User registered successfully: " + user.email);

        return userMapper.toDto(user);
    }

    /**
     * Login user
     */
    @Transactional
    public LoginResponse login(LoginRequest request, String ipAddress, String userAgent) {
        log.info("Login attempt for: " + request.email);

        // Find user by email
        User user = userRepository.findByEmail(request.email)
                .orElseThrow(() -> {
                    log.warn("Login failed - email not found: " + request.email);
                    return new AppException(ErrorCode.EMAIL_NOT_FOUND.code, ErrorCode.EMAIL_NOT_FOUND.message);
                });

        // Check if account is enabled
        if (!user.enabled) {
            throw new AppException(ErrorCode.ACCOUNT_DISABLED.code, ErrorCode.ACCOUNT_DISABLED.message);
        }

        // Check if account is locked
        if (!user.accountNonLocked) {
            throw new AppException(ErrorCode.ACCOUNT_LOCKED.code, ErrorCode.ACCOUNT_LOCKED.message);
        }

        // Verify password
        if (!BCrypt.checkpw(request.password, user.password)) {
            log.warn("Login failed - invalid password for: " + request.email);
            throw new AppException(ErrorCode.INVALID_PASSWORD.code, ErrorCode.INVALID_PASSWORD.message);
        }

        // Generate tokens
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        // Revoke existing sessions (single session per user)
        userSessionRepository.deactivateAllByUserId(user.id);

        // Create new session
        UserSession session = new UserSession();
        session.user = user;
        session.accessToken = accessToken;
        session.refreshToken = refreshToken;
        session.ipAddress = ipAddress;
        session.userAgent = userAgent;
        session.createdAt = LocalDateTime.now();
        session.expiresAt = LocalDateTime.now().plusSeconds(jwtService.getRefreshTokenExpiry());
        session.isActive = true;

        userSessionRepository.persist(session);

        log.info("User logged in successfully: " + user.email);

        return new LoginResponse(
                accessToken,
                refreshToken,
                jwtService.getAccessTokenExpiry(),
                userMapper.toDto(user)
        );
    }

    /**
     * Refresh access token
     */
    @Transactional
    public TokenResponse refreshToken(RefreshTokenRequest request) {
        log.info("Refreshing token...");

        // Check if token is blacklisted
        if (tokenBlacklistService.isBlacklisted(request.refreshToken)) {
            throw new AppException(ErrorCode.TOKEN_BLACKLISTED.code, ErrorCode.TOKEN_BLACKLISTED.message);
        }

        // Find session by refresh token
        UserSession session = userSessionRepository.findActiveByRefreshToken(request.refreshToken)
                .orElseThrow(() -> {
                    log.warn("Refresh token not found or session expired");
                    return new AppException(ErrorCode.SESSION_EXPIRED.code, ErrorCode.SESSION_EXPIRED.message);
                });

        // Check if session is still valid
        if (!session.isActive || session.expiresAt.isBefore(LocalDateTime.now())) {
            session.isActive = false;
            throw new AppException(ErrorCode.SESSION_EXPIRED.code, ErrorCode.SESSION_EXPIRED.message);
        }

        User user = session.user;

        // Generate new tokens
        String newAccessToken = jwtService.generateAccessToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);

        // Blacklist old tokens
        tokenBlacklistService.blacklistAccessToken(session.accessToken, "Token refreshed", session.createdAt.plusSeconds(jwtService.getAccessTokenExpiry()).atZone(java.time.ZoneId.systemDefault()).toInstant());
        tokenBlacklistService.blacklistRefreshToken(session.refreshToken, "Token refreshed", session.expiresAt.atZone(java.time.ZoneId.systemDefault()).toInstant());

        // Update session
        session.accessToken = newAccessToken;
        session.refreshToken = newRefreshToken;
        session.createdAt = LocalDateTime.now();
        session.expiresAt = LocalDateTime.now().plusSeconds(jwtService.getRefreshTokenExpiry());

        log.info("Token refreshed for user: " + user.email);

        return new TokenResponse(newAccessToken, newRefreshToken, jwtService.getAccessTokenExpiry());
    }

    /**
     * Logout user
     */
    @Transactional
    public void logout(LogoutRequest request) {
        log.info("Logout request received");

        if (request.accessToken != null) {
            tokenBlacklistService.blacklistAccessToken(
                    request.accessToken,
                    "User logout",
                    java.time.Instant.now().plusSeconds(jwtService.getAccessTokenExpiry())
            );
            userSessionRepository.deactivateByAccessToken(request.accessToken);
        }

        if (request.refreshToken != null) {
            tokenBlacklistService.blacklistRefreshToken(
                    request.refreshToken,
                    "User logout",
                    java.time.Instant.now().plusSeconds(jwtService.getRefreshTokenExpiry())
            );
        }

        log.info("User logged out successfully");
    }

    /**
     * Get current user info
     */
    public UserResponse getCurrentUser(Long userId) {
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new AppException(ErrorCode.USER_NOT_FOUND.code, ErrorCode.USER_NOT_FOUND.message);
        }
        return userMapper.toDto(user);
    }
}
