package com.example.util;

/**
 * Email Template Utility
 *
 * Contains HTML templates for emails
 */
public class EmailTemplateUtil {

    /**
     * Generate OTP email template
     */
    public static String getOtpEmailTemplate(String otp, int expiresInMinutes) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background-color: #4CAF50; color: white; padding: 20px; text-align: center; }
                    .content { padding: 30px; background-color: #f9f9f9; }
                    .otp-code { font-size: 32px; font-weight: bold; text-align: center;
                                letter-spacing: 10px; color: #4CAF50; margin: 20px 0; }
                    .footer { padding: 20px; text-align: center; font-size: 12px; color: #666; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>Password Reset Request</h1>
                    </div>
                    <div class="content">
                        <p>You have requested to reset your password.</p>
                        <p>Your One-Time Password (OTP) is:</p>
                        <div class="otp-code">%s</div>
                        <p><strong>Important:</strong></p>
                        <ul>
                            <li>This OTP expires in <strong>%d minutes</strong></li>
                            <li>Do not share this code with anyone</li>
                            <li>If you didn't request this, please ignore this email</li>
                        </ul>
                    </div>
                    <div class="footer">
                        <p>This is an automated message. Please do not reply.</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(otp, expiresInMinutes);
    }
}
