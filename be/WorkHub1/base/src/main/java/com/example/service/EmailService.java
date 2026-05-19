package com.example.service;

/**
 * Email Service Interface
 *
 * This interface defines the contract for sending emails.
 * Having an interface allows for:
 * - Easy testing with mock implementations
 * - Switching email providers without changing business logic
 * - Dependency injection in Quarkus CDI
 */
public interface EmailService {

    /**
     * Send an email
     * @param to Recipient email address
     * @param subject Email subject
     * @param content Email content (HTML or plain text)
     * @param isHtml Whether the content is HTML
     */
    void sendEmail(String to, String subject, String content, boolean isHtml);

    /**
     * Send an HTML email
     */
    default void sendHtmlEmail(String to, String subject, String content) {
        sendEmail(to, subject, content, true);
    }

    /**
     * Send a plain text email
     */
    default void sendTextEmail(String to, String subject, String content) {
        sendEmail(to, subject, content, false);
    }
}
