package com.igirerwanda.application_portal_backend.common.util;

import com.igirerwanda.application_portal_backend.config.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final JwtService jwtService;

    public String getCurrentUserId() {
        String token = extractTokenFromRequest();
        if (token != null) {
            return jwtService.extractUserId(token);
        }
        throw new SecurityException("No valid JWT token found");
    }

    public String getCurrentUserEmail() {
        String token = extractTokenFromRequest();
        if (token != null) {
            return jwtService.extractEmail(token);
        }
        throw new SecurityException("No valid JWT token found");
    }

    private String extractTokenFromRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();

            // 1. Check Header
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                return authHeader.substring(7);
            }

            // 2. Check Cookies
            if (request.getCookies() != null) {
                for (Cookie cookie : request.getCookies()) {
                    if ("access_token".equals(cookie.getName())) {
                        return cookie.getValue();
                    }
                }
            }
        }
        return null;
    }
}