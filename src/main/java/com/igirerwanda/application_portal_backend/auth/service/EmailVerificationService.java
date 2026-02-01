package com.igirerwanda.application_portal_backend.auth.service;

import com.igirerwanda.application_portal_backend.auth.entity.EmailVerificationToken;
import com.igirerwanda.application_portal_backend.auth.entity.Register;
import com.igirerwanda.application_portal_backend.auth.repository.EmailVerificationTokenRepository;
import com.igirerwanda.application_portal_backend.auth.repository.RegisterRepository;
import com.igirerwanda.application_portal_backend.common.enums.AuthProvider;
import com.igirerwanda.application_portal_backend.common.exception.NotFoundException;
import com.igirerwanda.application_portal_backend.common.exception.ValidationException;
import com.igirerwanda.application_portal_backend.notification.service.EmailService;
import jakarta.transaction.Transactional; // Ensure this is the correct import
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
public class EmailVerificationService {

    private final EmailVerificationTokenRepository tokenRepo;
    private final RegisterRepository registerRepo;
    private final EmailService emailService;
    private final UserPromotionService userPromotionService;

    public EmailVerificationService(
            EmailVerificationTokenRepository tokenRepo,
            RegisterRepository registerRepo,
            EmailService emailService,
            UserPromotionService userPromotionService) {
        this.tokenRepo = tokenRepo;
        this.registerRepo = registerRepo;
        this.emailService = emailService;
        this.userPromotionService = userPromotionService;
    }

    @Transactional
    public Map<String, String> verify(String tokenValue) {
        EmailVerificationToken token = tokenRepo.findByToken(tokenValue)
                .orElseThrow(() -> new ValidationException("Invalid verification token"));

        if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
            tokenRepo.delete(token);
            throw new ValidationException("Token has expired");
        }
        Register register = token.getRegister();

        if (!register.isVerified()) {
            register.setVerified(true);
            registerRepo.save(register);


            userPromotionService.promote(register);
        }

        tokenRepo.delete(token);
        return Map.of("message", "Email verified successfully");
    }

    @Transactional
    public Map<String, String> resendVerification(String email) {
        Register user = registerRepo.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (user.isVerified()) {
            return Map.of("message", "Email already verified");
        }

        if (user.getProvider() == AuthProvider.GOOGLE) {
            throw new ValidationException("Google accounts do not require email verification");
        }


        tokenRepo.deleteByRegister(user);

        tokenRepo.flush();

        EmailVerificationToken token = new EmailVerificationToken();
        token.setToken(UUID.randomUUID().toString());
        token.setRegister(user);
        token.setExpiryDate(LocalDateTime.now().plusHours(24));

        tokenRepo.save(token);

        emailService.sendVerificationEmail(user, token.getToken());

        return Map.of("message", "Verification email resent");
    }
}