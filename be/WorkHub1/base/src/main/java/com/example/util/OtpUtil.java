package com.example.util;

import jakarta.enterprise.context.ApplicationScoped;
import org.mindrot.jbcrypt.BCrypt;

import java.security.SecureRandom;

/**
 * OTP Utility for generating and validating OTP codes.
 *
 * SECURITY BEST PRACTICES:
 *
 * 1. Why hash OTP instead of storing plain text?
 *    - If DB is compromised, attackers cannot use OTP directly
 *    - Even if someone has DB access, they can't use the OTP
 *    - Adds defense in depth layer
 *
 * 2. Why use BCrypt for OTP?
 *    - BCrypt is slow by design (cost factor)
 *    - Built-in salt generation
 *    - Industry standard for password/secret hashing
 *
 * 3. Why not use regular hash (SHA-256)?
 *    - Too fast - can be brute forced easily for 6-digit OTP
 *    - No salt - rainbow table attacks possible
 *    - BCrypt's slowness makes brute force impractical
 *
 * 4. OTP best practices:
 *    - 6 digits = 1 million combinations
 *    - Expire after 60-120 seconds
 *    - One-time use only
 *    - Rate limit requests
 */
@ApplicationScoped
public class OtpUtil {

    private static final int OTP_LENGTH = 6;
    private static final String OTP_CHARACTERS = "0123456789";
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    // BCrypt cost factor - higher = slower but more secure
    // For OTP, we use a lower cost than passwords since OTP is short-lived
    private static final int BCRYPT_COST = 4;

    /**
     * Generate a 6-digit OTP
     */
    public String generateOtp() {
        StringBuilder otp = new StringBuilder(OTP_LENGTH);
        for (int i = 0; i < OTP_LENGTH; i++) {
            otp.append(OTP_CHARACTERS.charAt(SECURE_RANDOM.nextInt(OTP_CHARACTERS.length())));
        }
        return otp.toString();
    }

    /**
     * Hash OTP using BCrypt
     *
     * Why hash OTP?
     * - Even if DB is leaked, attacker cannot use hashed OTP
     * - BCrypt's salt ensures same OTP produces different hashes
     * - Slow hashing prevents brute force attacks
     */
    public String hashOtp(String otp) {
        return BCrypt.hashpw(otp, BCrypt.gensalt(BCRYPT_COST));
    }

    /**
     * Verify OTP against hashed value
     *
     * @param plainOtp The plain OTP from user input
     * @param hashedOtp The hashed OTP stored in DB
     * @return true if OTP matches
     */
    public boolean verifyOtp(String plainOtp, String hashedOtp) {
        try {
            return BCrypt.checkpw(plainOtp, hashedOtp);
        } catch (Exception e) {
            // Log potential attack attempt
            return false;
        }
    }

    /**
     * Validate OTP format
     */
    public boolean isValidFormat(String otp) {
        if (otp == null || otp.length() != OTP_LENGTH) {
            return false;
        }
        return otp.matches("^[0-9]{" + OTP_LENGTH + "}$");
    }
}
