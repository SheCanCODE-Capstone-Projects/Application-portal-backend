package com.igirerwanda.application_portal_backend.notification.service;

import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    public void sendPasswordResetEmail(String email, String resetToken) {
        String resetLink = "http://localhost:8080/reset-password?token=" + resetToken;
        String subject = "Password Reset Request";
        String body = "Click the following link to reset your password: " + resetLink + 
                     "\n\nThis link will expire in 5 minutes.";
        
        // Email sending implementation would go here
        System.out.println("Sending password reset email to: " + email);
        System.out.println("Reset link: " + resetLink);
    }
}
