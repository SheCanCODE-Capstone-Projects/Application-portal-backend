package com.igirerwanda.application_portal_backend.admin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminTestController {
    
    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> testAuth(Authentication auth) {
        return ResponseEntity.ok(Map.of(
            "message", "Access granted",
            "user", auth.getName(),
            "authorities", auth.getAuthorities()
        ));
    }
    
    @GetMapping("/test-secure")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Map<String, Object>> testSecure(Authentication auth) {
        return ResponseEntity.ok(Map.of(
            "message", "Secure admin access granted",
            "user", auth.getName(),
            "authorities", auth.getAuthorities()
        ));
    }
    
    @GetMapping("/debug-token")
    public ResponseEntity<Map<String, Object>> debugToken(Authentication auth) {
        if (auth == null) {
            return ResponseEntity.ok(Map.of("error", "No authentication found"));
        }
        return ResponseEntity.ok(Map.of(
            "authenticated", auth.isAuthenticated(),
            "principal", auth.getPrincipal(),
            "authorities", auth.getAuthorities(),
            "details", auth.getDetails()
        ));
    }
}