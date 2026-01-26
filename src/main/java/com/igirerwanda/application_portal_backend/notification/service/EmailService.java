package com.igirerwanda.application_portal_backend.notification.service;

import com.igirerwanda.application_portal_backend.auth.entity.Register;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final BrevoEmailService brevoEmailService;

    @Value("${app.frontend.base-url:http://localhost:3000/}")
    private String frontendBaseUrl;

    @Async
    public void sendEmail(String to, String subject, String htmlBody) {
        brevoEmailService.sendEmail(to, subject, htmlBody);
    }

    public void sendVerificationEmail(Register user, String token) {
        String baseUrl = frontendBaseUrl.endsWith("/") ? frontendBaseUrl : frontendBaseUrl + "/";
        String verificationLink = baseUrl + "verify-email?token=" + token;

        String subject = "Verify your email address";
        String htmlBody = """
                <html>
                <body style="font-family: Arial, sans-serif;">
                    <div style="max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 5px;">
                        <h2 style="color: #4CAF50;">Email Verification</h2>
                        <p>Hello <strong>%s</strong>,</p>
                        <p>Welcome to the Application Portal! Please verify your email by clicking the button below:</p>
                        <div style="text-align: center; margin: 30px 0;">
                            <a href="%s" style="background-color: #4CAF50; color: white; padding: 12px 25px; text-decoration: none; border-radius: 5px; font-weight: bold;">Verify Email</a>
                        </div>
                        <p>Or copy and paste this link into your browser:</p>
                        <p style="background-color: #f5f5f5; padding: 10px; word-break: break-all;"><small>%s</small></p>
                        <p><small>This link will expire in 24 hours.</small></p>
                    </div>
                </body>
                </html>
                """.formatted(user.getUsername(), verificationLink, verificationLink);

        sendEmail(user.getEmail(), subject, htmlBody);
    }
}