package com.igirerwanda.application_portal_backend.notification.service;

import com.igirerwanda.application_portal_backend.auth.entity.Register;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final SmtpEmailService smtpEmailService;

    public EmailService(SmtpEmailService smtpEmailService) {
        this.smtpEmailService = smtpEmailService;
    }

    public void sendEmail(String to, String subject, String body) {
        String htmlBody = "<html><body><pre>" + body + "</pre></body></html>";
        smtpEmailService.sendEmail(to, subject, htmlBody);
    }

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

        smtpEmailService.sendEmail(user.getEmail(), subject, htmlBody);
    }
}
