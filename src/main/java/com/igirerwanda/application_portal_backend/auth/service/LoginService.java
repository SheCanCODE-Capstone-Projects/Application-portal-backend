package com.igirerwanda.application_portal_backend.auth.service;

import com.igirerwanda.application_portal_backend.auth.dto.LoginRequest;
import com.igirerwanda.application_portal_backend.auth.dto.LoginResponse;
import com.igirerwanda.application_portal_backend.auth.entity.Register;
import com.igirerwanda.application_portal_backend.auth.repository.RegisterRepository;
import com.igirerwanda.application_portal_backend.common.exception.ValidationException;
import com.igirerwanda.application_portal_backend.config.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class LoginService {

    private final RegisterRepository registerRepo;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;

    public LoginService(RegisterRepository registerRepo,
                        PasswordEncoder encoder,
                        JwtService jwtService) {
        this.registerRepo = registerRepo;
        this.encoder = encoder;
        this.jwtService = jwtService;
    }

    public LoginResponse login(LoginRequest request) {

        Register user = registerRepo.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new ValidationException("Invalid credentials")
                );

        if (!user.isVerified()) {
            throw new ValidationException("Email not verified");
        }

        if (!encoder.matches(request.getPassword(), user.getPassword())) {
            throw new ValidationException("Invalid credentials");
        }

        String token = jwtService.generateToken(user);
        return new LoginResponse(token, user.getRole().name());
    }
}


