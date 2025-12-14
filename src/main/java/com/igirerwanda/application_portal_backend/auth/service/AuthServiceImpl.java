package com.igirerwanda.application_portal_backend.auth.service;

import com.igirerwanda.application_portal_backend.auth.dto.LoginRequest;
import com.igirerwanda.application_portal_backend.auth.dto.LoginResponse;
import com.igirerwanda.application_portal_backend.auth.entity.PasswordResetToken;
import com.igirerwanda.application_portal_backend.auth.repository.PasswordResetTokenRepository;
import com.igirerwanda.application_portal_backend.auth.repository.UserRepository;
import com.igirerwanda.application_portal_backend.common.enums.UserStatus;
import com.igirerwanda.application_portal_backend.config.JwtService;
import com.igirerwanda.application_portal_backend.notification.service.EmailService;
import com.igirerwanda.application_portal_backend.user.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private JwtService jwtService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    @Transactional
    public void initiatePasswordReset(String email) {
        // Always return same response to avoid enumeration
        // Check if user exists silently and send email only if exists
        try {
            // Find user by email (implementation depends on UserRepository)
            // If user exists:
            String token = generateSecureToken();
            // Long userId = user.getId();
            // PasswordResetToken resetToken = createResetToken(userId, token);
            // passwordResetTokenRepository.save(resetToken);
            // emailService.sendPasswordResetEmail(email, token);
        } catch (Exception e) {
            // Log error but don't expose to caller
        }
    }

    @Override
    @Transactional
    public void resetPassword(String token, String newPassword) {
        String tokenHash = hashToken(token);
        
        PasswordResetToken resetToken = passwordResetTokenRepository
            .findByTokenHashAndUsedFalseAndExpiresAtAfter(tokenHash, LocalDateTime.now())
            .orElseThrow(() -> new RuntimeException("Invalid or expired token"));

        // Update user password
        String hashedPassword = passwordEncoder.encode(newPassword);
        // userRepository.updatePassword(resetToken.getUserId(), hashedPassword);
        
        // Invalidate token
        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);
        
        // Revoke existing refresh tokens/sessions
        // jwtTokenRepository.deleteByUserId(resetToken.getUserId());
    }

    private String generateSecureToken() {
        byte[] tokenBytes = new byte[32];
        secureRandom.nextBytes(tokenBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Error hashing token", e);
        }
    }

    @Override
    public LoginResponse authenticate(LoginRequest request) {
        String normalizedEmail = request.getEmail().toLowerCase().trim();
        
        try {
            User user = userRepository.findByEmail(normalizedEmail).orElse(null);
            
            if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                logger.warn("Failed login attempt for email: {}", normalizedEmail);
                throw new RuntimeException("Invalid credentials");
            }
            
            if (user.getStatus() != UserStatus.ACTIVE) {
                logger.warn("Login attempt by unverified user: {}", normalizedEmail);
                throw new RuntimeException("Please verify your email before logging in.");
            }
            
            String token = jwtService.generateToken(user.getId(), user.getEmail(), user.getRole());
            
            logger.info("Successful login for user: {}", normalizedEmail);
            return new LoginResponse(token, user.getRole(), user.getId());
            
        } catch (Exception e) {
            logger.error("Authentication error for email: {}", normalizedEmail, e);
            throw e;
        }
    }

    private PasswordResetToken createResetToken(Long userId, String token) {
        String tokenHash = hashToken(token);
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(5);
        return new PasswordResetToken(userId, tokenHash, expiresAt);
    }
}
