package com.igirerwanda.application_portal_backend.auth.service;

import com.igirerwanda.application_portal_backend.auth.dto.LoginRequest;
import com.igirerwanda.application_portal_backend.auth.dto.LoginResponse;

public interface AuthService {
    void initiatePasswordReset(String email);
    void resetPassword(String token, String newPassword);
    LoginResponse authenticate(LoginRequest request);
}
