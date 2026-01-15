package com.igirerwanda.application_portal_backend.auth.service;


import com.igirerwanda.application_portal_backend.auth.dto.*;
import com.igirerwanda.application_portal_backend.auth.entity.RefreshToken;
import com.igirerwanda.application_portal_backend.common.exception.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AuthService {

    private final RegistrationService registrationService;
    private final EmailVerificationService emailVerificationService;
    private final LoginService loginService;
    private final PasswordResetService passwordResetService;
    private final RefreshTokenService refreshTokenService;
    
    @Value("${jwt.access.token.expiration:900000}")
    private long accessTokenExpiration;

    public AuthService(
            RegistrationService registrationService,
            EmailVerificationService emailVerificationService,
            LoginService loginService,
            PasswordResetService passwordResetService,
            RefreshTokenService refreshTokenService) {
        this.registrationService = registrationService;
        this.emailVerificationService = emailVerificationService;
        this.loginService = loginService;
        this.passwordResetService = passwordResetService;
        this.refreshTokenService = refreshTokenService;
    }

    public Map<String, String> register(RegisterRequest request) {
        return registrationService.register(request);
    }

    public Map<String, String> verifyEmail(VerifyEmailRequest request) {
        return emailVerificationService.verify(request.getToken());
    }

    public LoginResponse login(LoginRequest request) {
        return loginService.login(request);
    }

    public void initiatePasswordReset(String email) {
        passwordResetService.initiate(email);
    }

    public void resetPassword(String token, String newPassword) {
        passwordResetService.reset(token, newPassword);
    }

    public Map<String, String> resendVerification(String email) {
        return emailVerificationService.resendVerification(email);
    }
    
    public TokenRefreshResponse refreshToken(String refreshToken) {
        RefreshToken token = refreshTokenService.findByToken(refreshToken)
                .orElseThrow(() -> new ValidationException("Invalid refresh token"));
        
        token = refreshTokenService.verifyExpiration(token);
        
        // Generate new access token
        String newAccessToken = loginService.generateAccessToken(token.getUser());
        
        // Optionally rotate refresh token
        RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(token.getUser());
        
        return new TokenRefreshResponse(newAccessToken, newRefreshToken.getToken(), accessTokenExpiration);
    }
    
    public void logout(String refreshToken) {
        RefreshToken token = refreshTokenService.findByToken(refreshToken)
                .orElseThrow(() -> new ValidationException("Invalid refresh token"));
        
        refreshTokenService.revokeToken(token);
    }

}

