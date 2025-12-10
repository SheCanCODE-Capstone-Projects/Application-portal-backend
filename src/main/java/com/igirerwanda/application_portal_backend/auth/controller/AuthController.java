package com.igirerwanda.application_portal_backend.auth.controller;

import com.igirerwanda.application_portal_backend.auth.dto.RegisterRequest;
import com.igirerwanda.application_portal_backend.auth.service.AuthService;
import com.igirerwanda.application_portal_backend.common.enums.UserRole;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        // Prevent ADMIN role creation via public registration
        if (request.getRole() != null && 
            (request.getRole() == UserRole.ADMIN || 
             request.getRole() == UserRole.SUPER_ADMIN || 
             request.getRole() == UserRole.ADMIN_MANAGE)) {
            return ResponseEntity.badRequest()
                .body("Admin roles cannot be created via public registration");
        }
        
        // Force USER role for public registration
        request.setRole(UserRole.USER);
        
        return authService.register(request);
    }
}
