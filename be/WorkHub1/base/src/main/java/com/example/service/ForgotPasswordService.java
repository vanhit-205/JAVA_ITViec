package com.example.service;

import com.example.constant.ErrorCode;
import com.example.domain.dto.request.ChangePasswordRequest;
import com.example.domain.dto.request.VerifyEmailRequest;
import com.example.domain.dto.request.VerifyOtpRequest;
import com.example.domain.dto.response.OtpResponse;
import com.example.domain.entity.ForgotPassword;
import com.example.domain.entity.User;
import com.example.exception.AppException;
import com.example.repository.ForgotPasswordRepository;
import com.example.repository.UserRepository;
import com.example.util.EmailTemplateUtil;
import com.example.util.OtpUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.time.LocalDateTime;

/**
 * Forgot Password Service
 *
 * FLOW:
 * 1. verifyEmail() - User enters email -> generate OTP -> send email
 * 2. verifyOtp()   - User enters OTP -> validate -> mark as verified
 * 3. changePassword() - User enters new password -> update DB
 *
 * SECURITY MEASURES:
 * - OTP hashed before storage
 * - Rate limiting (60 second cooldown)
 * - OTP expires after 60-120 seconds
 * - OTP one-time use only
 * - IP tracking for audit
 */
@ApplicationScoped
public class ForgotPasswordService {

    private static final Logger log = Logger.getLogger(ForgotPasswordService.class);

    // OTP expires after 60 seconds
    private static final int OTP_EXPIRY_SECONDS = 60;

    // Rate limit: 1 OTP request per 60 seconds per email
    private static final int OTP_COOLDOWN_SECONDS = 60;

    @Inject
    UserRepository userRepository;

    @Inject
    ForgotPasswordRepository forgotPasswordRepository;

    @Inject
    OtpUtil otpUtil;

    @Inject
    EmailService emailService;

    /**
     * Step 1: Verify email and send OTP
     *
     * @param request Contains email address
     * @param ipAddress Client IP for security tracking
     * @return OTP response with message
     */
    @Transactional
    public OtpResponse verifyEmail(VerifyEmailRequest request, String ipAddress) {
        log.info("Processing forgot password request for: " + request.email + " from IP: " + ipAddress);

        // Find user by email
        User user = userRepository.findByEmail(request.email)
                .orElseThrow(() -> {
                    // Return generic message for security (don't reveal if email exists)
                    log.warn("Forgot password request for non-existent email: " + request.email);
                    return new AppException(ErrorCode.EMAIL_NOT_FOUND_FOR_RESET.code,
                            ErrorCode.EMAIL_NOT_FOUND_FOR_RESET.message);
                });

        // Check rate limit - has user requested OTP recently?
        LocalDateTime cooldownStart = LocalDateTime.now().minusSeconds(OTP_COOLDOWN_SECONDS);
        if (forgotPasswordRepository.existsRecentOtp(user.id, cooldownStart)) {
            log.warn("Rate limit exceeded for email: " + request.email);
            throw new AppException(ErrorCode.OTP_RATE_LIMITED.code, ErrorCode.OTP_RATE_LIMITED.message);
        }

        // Delete any existing unverified OTPs for this user
        forgotPasswordRepository.delete("user.id = ?1 and verified = false and used = false", user.id);

        // Generate OTP
        String plainOtp = otpUtil.generateOtp();
        String hashedOtp = otpUtil.hashOtp(plainOtp);

        // DEV MODE: Log OTP to console for testing
        log.info("===== DEV MODE: OTP for " + request.email + " is: " + plainOtp + " =====");

        // DEV MODE: Log OTP to console for testing
        log.info("===== DEV MODE: OTP for " + request.email + " is: " + plainOtp + " =====");

        // Create OTP record
        ForgotPassword forgotPassword = new ForgotPassword();
        forgotPassword.user = user;
        forgotPassword.hashedOtp = hashedOtp;
        forgotPassword.expirationTime = LocalDateTime.now().plusSeconds(OTP_EXPIRY_SECONDS);
        forgotPassword.ipAddress = ipAddress;
        forgotPassword.verified = false;
        forgotPassword.used = false;

        forgotPasswordRepository.persist(forgotPassword);

        // Send OTP via email
        String emailContent = EmailTemplateUtil.getOtpEmailTemplate(plainOtp, OTP_EXPIRY_SECONDS / 60);
        emailService.sendHtmlEmail(
                user.email,
                "Password Reset OTP",
                emailContent
        );

        log.info("OTP sent to: " + user.email);

        return new OtpResponse(
                "OTP sent to your email. Please check and enter the 6-digit code.",
                maskEmail(user.email),
                OTP_EXPIRY_SECONDS
        );
    }

    /**
     * Step 2: Verify OTP
     *
     * @param request Contains email and OTP
     * @return Success message if OTP is valid
     */
    @Transactional
    public String verifyOtp(VerifyOtpRequest request) {
        log.info("Verifying OTP for: " + request.email);
        log.info("User provided OTP: " + request.otp);

        // Find user
        User user = userRepository.findByEmail(request.email)
                .orElseThrow(() -> new AppException(ErrorCode.EMAIL_NOT_FOUND_FOR_RESET.code,
                        ErrorCode.EMAIL_NOT_FOUND_FOR_RESET.message));

        // Find valid OTP record
        ForgotPassword otpRecord = forgotPasswordRepository.findValidOtp(user.id)
                .orElseThrow(() -> {
                    log.warn("No valid OTP found for: " + request.email);
                    // Debug: list all OTP records
                    var allOtps = forgotPasswordRepository.list("user.id", user.id);
                    log.info("All OTP records for user: " + allOtps.size());
                    for (var otp : allOtps) {
                        log.info("OTP record - id: " + otp.id + ", verified: " + otp.verified + ", used: " + otp.used + ", expired: " + otp.isExpired());
                    }
                    return new AppException(ErrorCode.OTP_INVALID.code, ErrorCode.OTP_INVALID.message);
                });

        log.info("Found OTP record - id: " + otpRecord.id + ", hashedOtp length: " + otpRecord.hashedOtp.length());

        // Verify OTP (compare plain with hashed)
        boolean isValid = otpUtil.verifyOtp(request.otp, otpRecord.hashedOtp);
        log.info("OTP verification result: " + isValid);

        if (!isValid) {
            log.warn("Invalid OTP provided for: " + request.email);
            throw new AppException(ErrorCode.OTP_INVALID.code, ErrorCode.OTP_INVALID.message);
        }

        // Check if expired
        if (otpRecord.isExpired()) {
            log.warn("Expired OTP provided for: " + request.email);
            throw new AppException(ErrorCode.OTP_EXPIRED.code, ErrorCode.OTP_EXPIRED.message);
        }

        // Mark as verified (one-time use)
        otpRecord.verified = true;
        forgotPasswordRepository.persist(otpRecord);
        forgotPasswordRepository.flush(); // Force commit to DB immediately

        log.info("OTP verified successfully for: " + request.email);

        return "OTP verified successfully. Please enter your new password.";
    }

    /**
     * Step 3: Change password
     *
     * @param request Contains email, OTP, and new password
     * @return Success message
     */
    @Transactional
    public String changePassword(ChangePasswordRequest request) {
        log.info("Processing password change for: " + request.email);

        // Validate password match
        if (!request.newPassword.equals(request.repeatPassword)) {
            log.warn("Passwords do not match for: " + request.email);
            throw new AppException(ErrorCode.INVALID_REPEAT_PASSWORD.code,
                    ErrorCode.INVALID_REPEAT_PASSWORD.message);
        }

        // Find user
        User user = userRepository.findByEmail(request.email)
                .orElseThrow(() -> new AppException(ErrorCode.EMAIL_NOT_FOUND_FOR_RESET.code,
                        ErrorCode.EMAIL_NOT_FOUND_FOR_RESET.message));

        // Find verified OTP
        ForgotPassword otpRecord = forgotPasswordRepository
                .find("user.id = ?1 and verified = true and used = false and expirationTime > ?2",
                        user.id, LocalDateTime.now())
                .firstResultOptional()
                .orElseThrow(() -> {
                    log.warn("No verified OTP found for password change: " + request.email);
                    return new AppException(ErrorCode.OTP_INVALID.code, ErrorCode.OTP_INVALID.message);
                });

        // Change password (BCrypt hash)
        user.password = org.mindrot.jbcrypt.BCrypt.hashpw(request.newPassword, org.mindrot.jbcrypt.BCrypt.gensalt());
        userRepository.persist(user);

        // Mark OTP as used
        otpRecord.used = true;
        forgotPasswordRepository.persist(otpRecord);

        // Delete all other OTP records for this user (cleanup)
        forgotPasswordRepository.delete("user.id = ?1 and id != ?2", user.id, otpRecord.id);

        log.info("Password changed successfully for: " + request.email);

        return "Password changed successfully. You can now login with your new password.";
    }

    /**
     * Mask email for logging/response (e.g., t***@gmail.com)
     */
    private String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return email;
        }
        String[] parts = email.split("@");
        String name = parts[0];
        String domain = parts[1];

        if (name.length() <= 2) {
            return name.charAt(0) + "***@" + domain;
        }
        return name.charAt(0) + "***@" + domain;
    }
}
