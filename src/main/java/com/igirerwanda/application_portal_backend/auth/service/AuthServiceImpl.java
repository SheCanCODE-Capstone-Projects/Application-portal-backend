package com.igirerwanda.application_portal_backend.auth.service;

import com.igirerwanda.application_portal_backend.auth.dto.RegisterRequest;
import com.igirerwanda.application_portal_backend.auth.dto.RegisterResponse;
import com.igirerwanda.application_portal_backend.auth.entity.EmailVerificationToken;
import com.igirerwanda.application_portal_backend.auth.repository.UserRepository;
import com.igirerwanda.application_portal_backend.common.enums.UserRole;
import com.igirerwanda.application_portal_backend.common.enums.UserStatus;
import com.igirerwanda.application_portal_backend.common.exception.DuplicateResourceException;
import com.igirerwanda.application_portal_backend.common.util.ValidationUtil;
import com.igirerwanda.application_portal_backend.notification.service.EmailService;
import com.igirerwanda.application_portal_backend.user.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private EmailService emailService;
    
    @Override
    public RegisterResponse register(RegisterRequest request) {
        // Basic input validation (using @Valid annotation handles most of this)
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (request.getPassword() == null || request.getPassword().length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters");
        }
        
        // Normalize email to lowercase
        String normalizedEmail = request.getEmail().toLowerCase();
        
        // Check if user already exists
        if (userRepository.findByEmail(normalizedEmail).isPresent()) {
            throw new RuntimeException("User with email already exists");
        }
        
        // Create user
        User user = new User();
        user.setEmail(normalizedEmail);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhone(request.getPhone());
        user.setRole(UserRole.APPLICANT);
        user.setStatus(UserStatus.PENDING_VERIFICATION);
        
        // Save user
        User savedUser = userRepository.save(user);
        
        // Create verification token
        EmailVerificationToken token = new EmailVerificationToken();
        token.setToken(UUID.randomUUID().toString());
        token.setUser(savedUser);
        token.setExpiryDate(LocalDateTime.now().plusHours(24));
        
        // Send verification email (if EmailService has the method)
        try {
            // emailService.sendVerificationEmail(savedUser.getEmail(), token.getToken());
            // TODO: Implement email sending when EmailService is ready
            System.out.println("Email verification token generated: " + token.getToken());
        } catch (Exception e) {
            // Email sending failed, but user creation succeeded
        }
        
        return new RegisterResponse(
            savedUser.getId(),
            savedUser.getEmail(),
            savedUser.getRole().toString(),
            "Registration successful. Please check your email for verification."
        );
    }
}
