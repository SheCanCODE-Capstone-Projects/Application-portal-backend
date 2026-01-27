package com.igirerwanda.application_portal_backend.auth.service;

import com.igirerwanda.application_portal_backend.auth.dto.RegisterRequest;
import com.igirerwanda.application_portal_backend.auth.entity.EmailVerificationToken;
import com.igirerwanda.application_portal_backend.auth.entity.Register;
import com.igirerwanda.application_portal_backend.auth.repository.EmailVerificationTokenRepository;
import com.igirerwanda.application_portal_backend.auth.repository.RegisterRepository;
import com.igirerwanda.application_portal_backend.common.enums.UserRole;
import com.igirerwanda.application_portal_backend.common.exception.DuplicateResourceException;
import com.igirerwanda.application_portal_backend.common.exception.ValidationException;
import com.igirerwanda.application_portal_backend.common.util.PasswordUtil;
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
        // 1. Enhanced Validation
        if (registerRepo.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("This email address is already registered.");
        }

        if (!PasswordUtil.isStrongPassword(request.getPassword())) {
            throw new ValidationException(PasswordUtil.getPasswordRequirements());
        }

        // 2. Create User
        Register user = new Register();
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setPassword(encoder.encode(request.getPassword()));
        user.setRole(UserRole.APPLICANT);
        user.setVerified(false);

        Register savedUser = registerRepo.save(user);


        try {
            webSocketEventService.broadcastUserEvent("REGISTERED", Map.of(
                    "id", savedUser.getId().toString(),
                    "email", savedUser.getEmail(),
                    "username", savedUser.getUsername(),
                    "timestamp", LocalDateTime.now().toString()
            ));
        } catch (Exception e) {
            // Log but allow registration to proceed
            System.err.println("WebSocket broadcast failed: " + e.getMessage());
        }

        // 4. Generate Verification Token
        EmailVerificationToken token = new EmailVerificationToken();
        token.setToken(UUID.randomUUID().toString());
        token.setRegister(savedUser);
        token.setExpiryDate(LocalDateTime.now().plusHours(24));

        tokenRepo.save(token);

        // 5. Send Verification Email
        emailService.sendVerificationEmail(savedUser, token.getToken());

        return Map.of("message", "Registration successful! Please check your email to verify your account.");
    }
}