package com.igirerwanda.application_portal_backend.auth.service;

import com.igirerwanda.application_portal_backend.auth.dto.LoginResponse;
import com.igirerwanda.application_portal_backend.auth.entity.Register;
import com.igirerwanda.application_portal_backend.auth.repository.RegisterRepository;
import com.igirerwanda.application_portal_backend.common.enums.AuthProvider;
import com.igirerwanda.application_portal_backend.common.enums.UserRole;
import com.igirerwanda.application_portal_backend.common.exception.DuplicateResourceException;
import com.igirerwanda.application_portal_backend.common.exception.NotFoundException;
import com.igirerwanda.application_portal_backend.common.exception.ValidationException;
import com.igirerwanda.application_portal_backend.config.JwtService;
import org.springframework.stereotype.Service;

@Service
public class GoogleAuthService {

    private final RegisterRepository registerRepo;
    private final JwtService jwtService;

    public GoogleAuthService(RegisterRepository registerRepo, JwtService jwtService) {
        this.registerRepo = registerRepo;
        this.jwtService = jwtService;
    }


    public Register signupWithGoogle(String email, String googleId, String name) {

        if (registerRepo.findByEmail(email).isPresent()) {
            throw new DuplicateResourceException(
                    "User already exists, please login"
            );
        }

        Register user = new Register();
        user.setEmail(email);
        user.setGoogleId(googleId);
        user.setUsername(name);
        user.setVerified(true);
        user.setProvider(AuthProvider.GOOGLE);
        user.setRole(UserRole.APPLICANT);

        return registerRepo.save(user);
    }


    public LoginResponse loginWithGoogle(String email, String googleId) {

        Register user = registerRepo.findByEmail(email)
                .orElseThrow(() ->
                        new NotFoundException("User not registered")
                );

        if (!googleId.equals(user.getGoogleId())) {
            throw new ValidationException("Invalid Google account");
        }

        String token = jwtService.generateAccessToken(user);
        return new LoginResponse(token, user.getRole().name());
    }
}

