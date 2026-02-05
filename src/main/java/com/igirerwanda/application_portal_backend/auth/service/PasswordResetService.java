package com.igirerwanda.application_portal_backend.auth.service;

import com.igirerwanda.application_portal_backend.auth.entity.PasswordResetToken;
import com.igirerwanda.application_portal_backend.auth.entity.Register;
import com.igirerwanda.application_portal_backend.auth.exception.BadRequestException;
import com.igirerwanda.application_portal_backend.auth.repository.PasswordResetTokenRepository;
import com.igirerwanda.application_portal_backend.auth.repository.RegisterRepository;
import com.igirerwanda.application_portal_backend.common.exception.NotFoundException;
import com.igirerwanda.application_portal_backend.common.exception.ValidationException;
import com.igirerwanda.application_portal_backend.notification.service.EmailService;
import org.springframework.beans.factory.annotation.Value; // Import added
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PasswordResetService {

    private final RegisterRepository registerRepo;
    private final PasswordResetTokenRepository tokenRepo;
    private final PasswordEncoder encoder;
    private final EmailService emailService;

    // Inject Frontend URL
    @Value("${app.frontend.base-url}")
    private String frontendBaseUrl;

    private static final int MAX_RESET_REQUESTS = 3;

    public PasswordResetService(RegisterRepository registerRepo,
                                PasswordResetTokenRepository tokenRepo,
                                PasswordEncoder encoder,
                                EmailService emailService) {
        this.registerRepo = registerRepo;
        this.tokenRepo = tokenRepo;
        this.encoder = encoder;
        this.emailService = emailService;
    }

    public void initiate(String email) {

        Register user = registerRepo.findByEmail(email)
                .orElseThrow(() ->
                        new NotFoundException("User not found with email: " + email)
                );

        long requestCount = tokenRepo.countByRegister(user);
        if (requestCount >= MAX_RESET_REQUESTS) {
            throw new ValidationException(
                    "Maximum password reset requests reached. Try again later."
            );
        }

        tokenRepo.findByRegister(user).ifPresent(existingToken -> {
            if (existingToken.getExpiryDate().isAfter(LocalDateTime.now())) {
                throw new ValidationException(
                        "A valid reset token already exists. Please wait until it expires."
                );
            } else {
                tokenRepo.delete(existingToken);
            }
        });

        PasswordResetToken token = new PasswordResetToken();
        token.setToken(UUID.randomUUID().toString());
        token.setRegister(user);
        token.setExpiryDate(LocalDateTime.now().plusMinutes(1));

        tokenRepo.save(token);

        // FIXED: Use frontend URL and Path Parameter
        if (frontendBaseUrl == null || frontendBaseUrl.trim().isEmpty()) {
                        throw new IllegalStateException("app.frontend.base-url must be configured");
                    }
              String baseUrl = frontendBaseUrl.trim();

        baseUrl = baseUrl.endsWith("/") ? baseUrl : baseUrl + "/";
        String resetLink = baseUrl + "reset-password/" + token.getToken();

        emailService.sendEmail(
                user.getEmail(),
                "Password Reset Request",
                "Click the link to reset your password (expires in 1 minute): " + resetLink
        );
    }

    public void reset(String tokenValue, String newPassword) {

        PasswordResetToken token = tokenRepo.findByToken(tokenValue)
                .orElseThrow(() ->
                        new ValidationException("Invalid password reset token")
                );

        if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
            tokenRepo.delete(token);
            throw new ValidationException("Password reset token has expired");
        }

        Register user = token.getRegister();
        user.setPassword(encoder.encode(newPassword));

        registerRepo.save(user);
        tokenRepo.delete(token);
    }
}