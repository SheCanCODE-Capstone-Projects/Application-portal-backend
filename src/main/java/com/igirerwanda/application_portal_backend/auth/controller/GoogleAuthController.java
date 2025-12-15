package com.igirerwanda.application_portal_backend.auth.controller;

import com.igirerwanda.application_portal_backend.auth.dto.GoogleAuthDto;
import com.igirerwanda.application_portal_backend.auth.service.GoogleAuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class GoogleAuthController {

    private final GoogleAuthService googleAuthService;

    public GoogleAuthController(GoogleAuthService googleAuthService) {
        this.googleAuthService = googleAuthService;
    }


    @PostMapping("/google/signup")
    public ResponseEntity<?> googleSignup(@RequestBody GoogleAuthDto dto) {

        var user = googleAuthService.signupWithGoogle(
                dto.getEmail(),
                dto.getGoogleId(),
                dto.getName()
        );

        return ResponseEntity.ok(Map.of(
                "message", "Google signup successful",
                "userId", user.getId()
        ));
    }


    @PostMapping("/google/login")
    public ResponseEntity<?> googleLogin(@RequestBody GoogleAuthDto dto) {

        return ResponseEntity.ok(
                googleAuthService.loginWithGoogle(
                        dto.getEmail(),
                        dto.getGoogleId()
                )
        );
    }
}
