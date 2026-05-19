package com.example.security;

import com.example.domain.entity.User;
import com.example.repository.UserRepository;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.logging.Logger;

/**
 * Request-scoped security context to access current user info
 */
@RequestScoped
public class SecurityContext {

    private static final Logger log = Logger.getLogger(SecurityContext.class);

    @Inject
    SecurityIdentity securityIdentity;

    @Inject
    JsonWebToken jwt;

    @Inject
    UserRepository userRepository;

    /**
     * Get current user from database using JWT subject
     */
    public User getCurrentUser() {
        try {
            String subject = jwt.getSubject();
            if (subject != null) {
                Long userId = Long.parseLong(subject);
                return userRepository.findById(userId);
            }
        } catch (Exception e) {
            log.warn("Failed to get current user", e);
        }
        return null;
    }

    /**
     * Get current user ID from JWT
     */
    public Long getCurrentUserId() {
        try {
            String subject = jwt.getSubject();
            return subject != null ? Long.parseLong(subject) : null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Get current user email from JWT
     */
    public String getCurrentUserEmail() {
        return jwt.getClaim("email");
    }

    /**
     * Get current user role from JWT
     */
    public String getCurrentUserRole() {
        var groups = jwt.getGroups();
        return groups != null && !groups.isEmpty() ? groups.iterator().next() : null;
    }

    /**
     * Check if current user has specific role
     */
    public boolean hasRole(String role) {
        return securityIdentity.hasRole(role);
    }

    /**
     * Check if user is authenticated
     */
    public boolean isAuthenticated() {
        return !securityIdentity.isAnonymous();
    }

    /**
     * Get current user's company ID from JWT claims
     */
    public Long getCurrentCompanyId() {
        try {
            var companyId = jwt.getClaim("companyId");
            if (companyId != null) {
                if (companyId instanceof Integer) {
                    return ((Integer) companyId).longValue();
                } else if (companyId instanceof Long) {
                    return (Long) companyId;
                } else if (companyId instanceof String) {
                    return Long.parseLong((String) companyId);
                }
            }
        } catch (Exception e) {
            log.warn("Failed to get current company ID", e);
        }
        return null;
    }
}
