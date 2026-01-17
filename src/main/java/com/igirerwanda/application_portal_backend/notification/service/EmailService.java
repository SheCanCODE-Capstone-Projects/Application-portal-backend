package com.igirerwanda.application_portal_backend.notification.service;

import com.igirerwanda.application_portal_backend.auth.entity.Register;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);

            System.out.println("Email sent to: " + to);
        } catch (Exception e) {
            System.err.println("Failed to send email: " + e.getMessage());
        }
    }

    // Add this for verification emails
    public void sendVerificationEmail(Register user, String token) {
        String verificationLink = "http://localhost:3000/api/auth/verify-email?token=" + token;

        String subject = "Verify your email address";
        String body = """
                Hello %s,

                Please verify your email by clicking the link below:

                %s

                This link will expire in 24 hours.

                If you did not create an account, please ignore this email.
                """.formatted(user.getUsername(), verificationLink);

        sendEmail(user.getEmail(), subject, body);
    }
}
