package com.igirerwanda.application_portal_backend.auth.service;

import com.igirerwanda.application_portal_backend.auth.dto.LoginRequest;
import com.igirerwanda.application_portal_backend.auth.dto.LoginResponse;
import com.igirerwanda.application_portal_backend.auth.entity.RefreshToken;
import com.igirerwanda.application_portal_backend.auth.entity.Register;
import com.igirerwanda.application_portal_backend.auth.repository.RegisterRepository;
import com.igirerwanda.application_portal_backend.common.exception.ValidationException;
import com.igirerwanda.application_portal_backend.config.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final RegisterRepository registerRepo;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    @Value("${jwt.access.token.expiration:900000}")
    private long accessTokenExpiration;

    public LoginResponse login(LoginRequest request) {
        Register user = registerRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new ValidationException("Invalid email or password"));

        if (!user.isVerified()) {
            throw new ValidationException("Account is not verified. Please check your email.");
        }

        if (!encoder.matches(request.getPassword(), user.getPassword())) {
            throw new ValidationException("Invalid email or password");
        }

        String accessToken = jwtService.generateToken(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        return new LoginResponse(accessToken, refreshToken.getToken(), accessTokenExpiration);
    }

    public String generateAccessToken(Register user) {
        return jwtService.generateToken(user);
    }
}