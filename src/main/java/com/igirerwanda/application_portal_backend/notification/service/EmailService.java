package com.igirerwanda.application_portal_backend.notification.service;

public interface EmailService {
    void sendPasswordResetEmail(String email, String resetToken);
    void sendVerificationEmail(String email, String verificationToken);
}
