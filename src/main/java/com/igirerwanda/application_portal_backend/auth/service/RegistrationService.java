package com.igirerwanda.application_portal_backend.auth.service;

import com.igirerwanda.application_portal_backend.auth.dto.RegisterRequest;
import com.igirerwanda.application_portal_backend.auth.entity.EmailVerificationToken;
import com.igirerwanda.application_portal_backend.auth.entity.Register;
import com.igirerwanda.application_portal_backend.auth.repository.EmailVerificationTokenRepository;
import com.igirerwanda.application_portal_backend.auth.repository.RegisterRepository;
import com.igirerwanda.application_portal_backend.common.enums.UserRole;
import com.igirerwanda.application_portal_backend.common.exception.DuplicateResourceException;
import com.igirerwanda.application_portal_backend.common.exception.ValidationException;
import com.igirerwanda.application_portal_backend.notification.service.EmailService;
import com.igirerwanda.application_portal_backend.notification.service.WebSocketService;
import com.igirerwanda.application_portal_backend.user.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
public class RegistrationService {

    private final RegisterRepository registerRepo;
    private final EmailVerificationTokenRepository tokenRepo;
    private final PasswordEncoder encoder;
    private final EmailService emailService;
    private final WebSocketService webSocketService;
    private final UserPromotionService userPromotionService;

    public RegistrationService(RegisterRepository registerRepo,
                               EmailVerificationTokenRepository tokenRepo,
                               PasswordEncoder encoder,
                               EmailService emailService,
                               WebSocketService webSocketService, UserPromotionService userPromotionService) {
        this.registerRepo = registerRepo;
        this.tokenRepo = tokenRepo;
        this.encoder = encoder;
        this.emailService = emailService;
        this.webSocketService = webSocketService;
        this.userPromotionService = userPromotionService;
    }

    public Map<String, String> register(RegisterRequest request) {

        if (registerRepo.findByEmail(request.getEmail()).isPresent()) {
            throw new DuplicateResourceException("Email already registered");
        }

        if (registerRepo.findByUsername(request.getUsername()).isPresent()) {
            throw new DuplicateResourceException("Username already taken");
        }

        Register user = new Register();
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setPassword(encoder.encode(request.getPassword()));
        user.setRole(UserRole.APPLICANT);
        user.setVerified(false);

        registerRepo.save(user);

        webSocketService.broadcastToAdmin("users", user);

        EmailVerificationToken token = new EmailVerificationToken();
        token.setToken(UUID.randomUUID().toString());
        token.setRegister(user);
        token.setExpiryDate(LocalDateTime.now().plusHours(24));

        tokenRepo.save(token);

        String link = "http://localhost:8080/api/v1/auth/verify?token=" + token.getToken();
        emailService.sendEmail(
                user.getEmail(),
                "Verify your account",
                "Click here to verify: " + link
        );

        return Map.of("message", "Verification email sent");

    }

    @Transactional
    public User verifyEmail(String tokenStr) {
        EmailVerificationToken token = tokenRepo.findByToken(tokenStr)
                .orElseThrow(() -> new ValidationException("Invalid or expired verification token"));

        // SECURITY FIX: Check if token is expired
        if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
            tokenRepo.delete(token);
            throw new ValidationException("Verification token has expired");
        }

        Register register = token.getRegister();
        register.setVerified(true);
        registerRepo.save(register);

        User user = userPromotionService.promote(register);


        tokenRepo.delete(token);

        return user;
    }

}