package com.igirerwanda.application_portal_backend.auth.service;

import com.igirerwanda.application_portal_backend.auth.dto.RegisterRequest;
import com.igirerwanda.application_portal_backend.auth.entity.EmailVerificationToken;
import com.igirerwanda.application_portal_backend.auth.entity.Register;
import com.igirerwanda.application_portal_backend.auth.repository.EmailVerificationTokenRepository;
import com.igirerwanda.application_portal_backend.auth.repository.RegisterRepository;
import com.igirerwanda.application_portal_backend.common.enums.UserRole;
import com.igirerwanda.application_portal_backend.common.exception.DuplicateResourceException;
import com.igirerwanda.application_portal_backend.notification.service.EmailService;
import com.igirerwanda.application_portal_backend.notification.service.WebSocketEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final RegisterRepository registerRepo;
    private final EmailVerificationTokenRepository tokenRepo;
    private final PasswordEncoder encoder;
    private final EmailService emailService;
    private final WebSocketEventService webSocketEventService;

    @Transactional
    public Map<String, String> register(RegisterRequest request) {

        if (registerRepo.findByEmail(request.getEmail()).isPresent()) {
            throw new DuplicateResourceException("Email already registered");
        }

        Register user = new Register();
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setPassword(encoder.encode(request.getPassword()));
        user.setRole(UserRole.APPLICANT);
        user.setVerified(false);

        registerRepo.save(user);

        // Broadcast user registration to admins
        try {
            webSocketEventService.broadcastUserEvent("REGISTERED", Map.of(
                    "email", user.getEmail(),
                    "username", user.getUsername(),
                    "role", user.getRole().toString()
            ));
        } catch (Exception e) {
            // Log but don't fail registration if socket fails
            System.err.println("WebSocket broadcast failed: " + e.getMessage());
        }

        // Generate verification token
        EmailVerificationToken token = new EmailVerificationToken();
        token.setToken(UUID.randomUUID().toString());
        token.setRegister(user);
        token.setExpiryDate(LocalDateTime.now().plusHours(24));

        tokenRepo.save(token);

        // Send verification email
        emailService.sendVerificationEmail(user, token.getToken());

        return Map.of("message", "Verification email sent");
    }
}