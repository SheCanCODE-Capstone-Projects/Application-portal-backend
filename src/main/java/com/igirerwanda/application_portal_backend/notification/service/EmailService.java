package com.igirerwanda.application_portal_backend.notification.service;

import com.igirerwanda.application_portal_backend.auth.entity.Register;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final ResendEmailService resendEmailService;

    public EmailService(ResendEmailService resendEmailService) {
        this.resendEmailService = resendEmailService;
    }

    public void sendEmail(String to, String subject, String body) {
        // Convert plain text to HTML
        String htmlBody = "<html><body><pre>" + body + "</pre></body></html>";
        resendEmailService.sendEmail(to, subject, htmlBody);
    }

    // Add this for verification emails
    public void sendVerificationEmail(Register user, String token) {
        String verificationLink = "http://localhost:8080/api/auth/verify-email?token=" + token;

        String subject = "Verify your email address";
        String htmlBody = """
                <html>
                <body>
                    <h2>Email Verification</h2>
                    <p>Hello %s,</p>
                    <p>Please verify your email by clicking the link below:</p>
                    <p><a href="%s" style="background-color: #4CAF50; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px;">Verify Email</a></p>
                    <p>Or copy and paste this link: <br>%s</p>
                    <p><small>This link will expire in 24 hours.</small></p>
                    <p><small>If you did not create an account, please ignore this email.</small></p>
                </body>
                </html>
                """.formatted(user.getUsername(), verificationLink, verificationLink);

        resendEmailService.sendEmail(user.getEmail(), subject, htmlBody);
    }
}
