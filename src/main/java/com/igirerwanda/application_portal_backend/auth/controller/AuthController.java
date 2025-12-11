package com.igirerwanda.application_portal_backend.auth.controller;

import com.igirerwanda.application_portal_backend.auth.dto.PasswordResetDto;
import com.igirerwanda.application_portal_backend.auth.dto.PasswordResetRequest;
import com.igirerwanda.application_portal_backend.auth.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/password")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/forgot")
    public ResponseEntity<Map<String, String>> forgotPassword(@RequestBody PasswordResetRequest request) {
        authService.initiatePasswordReset(request.getEmail());
        return ResponseEntity.ok(Map.of("message", "If an account exists for this email, a reset link has been sent."));
    }

    @PostMapping("/reset")
    public ResponseEntity<Map<String, String>> resetPassword(@RequestBody PasswordResetDto request) {
        authService.resetPassword(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok(Map.of("message", "Password reset successful. Please login."));
    }
}
