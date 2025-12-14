package com.igirerwanda.application_portal_backend.auth.controller;

import com.igirerwanda.application_portal_backend.auth.dto.PasswordResetDto;
import com.igirerwanda.application_portal_backend.auth.dto.PasswordResetRequest;
import com.igirerwanda.application_portal_backend.auth.dto.ResendVerificationRequest;
import com.igirerwanda.application_portal_backend.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Authentication", description = "Authentication and verification endpoints")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/password/forgot")
    public ResponseEntity<Map<String, String>> forgotPassword(@RequestBody PasswordResetRequest request) {
        authService.initiatePasswordReset(request.getEmail());
        return ResponseEntity.ok(Map.of("message", "If an account exists for this email, a reset link has been sent."));
    }

    @PostMapping("/password/reset")
    public ResponseEntity<Map<String, String>> resetPassword(@RequestBody PasswordResetDto request) {
        authService.resetPassword(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok(Map.of("message", "Password reset successful. Please login."));
    }

    @PostMapping("/verify/resend")
    @Operation(summary = "Resend email verification", description = "Resend verification email for unverified users")
    @ApiResponse(responseCode = "200", description = "Verification email sent successfully")
    @ApiResponse(responseCode = "409", description = "User already verified")
    public ResponseEntity<Map<String, String>> resendVerificationEmail(@Valid @RequestBody ResendVerificationRequest request) {
        try {
            authService.resendVerificationEmail(request.getEmail());
            return ResponseEntity.ok(Map.of("message", "A new verification email has been sent."));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("User already verified")) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "User already verified"));
            }
            return ResponseEntity.ok(Map.of("message", "A new verification email has been sent."));
        }
    }
}
