package com.igirerwanda.application_portal_backend.auth.service;


import com.igirerwanda.application_portal_backend.auth.dto.LoginRequest;
import com.igirerwanda.application_portal_backend.auth.dto.LoginResponse;
import com.igirerwanda.application_portal_backend.auth.dto.RegisterRequest;
import com.igirerwanda.application_portal_backend.auth.dto.VerifyEmailRequest;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AuthService {

    private final RegistrationService registrationService;
    private final EmailVerificationService emailVerificationService;
    private final LoginService loginService;
    private final PasswordResetService passwordResetService;

    public AuthService(
            RegistrationService registrationService,
            EmailVerificationService emailVerificationService,
            LoginService loginService,
            PasswordResetService passwordResetService) {
        this.registrationService = registrationService;
        this.emailVerificationService = emailVerificationService;
        this.loginService = loginService;
        this.passwordResetService = passwordResetService;
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

}

