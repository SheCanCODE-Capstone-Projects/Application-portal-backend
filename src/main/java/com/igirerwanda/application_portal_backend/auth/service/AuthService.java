package com.igirerwanda.application_portal_backend.auth.service;

public interface AuthService {
    void initiatePasswordReset(String email);
    void resetPassword(String token, String newPassword);
}
