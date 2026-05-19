package com.example.security;

import io.smallrye.jwt.auth.principal.JWTAuthContextInfo;
import io.smallrye.jwt.auth.principal.ParseException;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;
import jakarta.inject.Inject;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.logging.Logger;

import java.util.Set;

/**
 * Custom JWT principal that extends the standard JsonWebToken
 */
@ApplicationScoped
public class JwtPrincipal {

    private static final Logger log = Logger.getLogger(JwtPrincipal.class);

    /**
     * Extract user ID from JWT token
     */
    public Long getUserId(JsonWebToken jwt) {
        try {
            Object userId = jwt.getClaim("userId");
            if (userId instanceof Long) {
                return (Long) userId;
            } else if (userId instanceof Integer) {
                return ((Integer) userId).longValue();
            } else if (userId instanceof String) {
                return Long.parseLong((String) userId);
            }
        } catch (Exception e) {
            log.warn("Failed to extract userId from token", e);
        }
        return null;
    }

    /**
     * Extract email from JWT token
     */
    public String getEmail(JsonWebToken jwt) {
        return jwt.getClaim("email");
    }

    /**
     * Extract username from JWT token
     */
    public String getUsername(JsonWebToken jwt) {
        return jwt.getClaim("username");
    }

    /**
     * Extract role from JWT token
     */
    public String getRole(JsonWebToken jwt) {
        Set<String> groups = jwt.getGroups();
        if (groups != null && !groups.isEmpty()) {
            return groups.iterator().next();
        }
        return null;
    }
}
