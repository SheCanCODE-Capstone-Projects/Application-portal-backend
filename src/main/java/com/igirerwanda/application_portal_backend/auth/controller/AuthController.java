package com.igirerwanda.application_portal_backend.auth.controller;

import com.igirerwanda.application_portal_backend.auth.dto.*;
import com.igirerwanda.application_portal_backend.auth.service.AuthService;
import com.igirerwanda.application_portal_backend.security.RateLimitingService;
import com.igirerwanda.application_portal_backend.common.exception.ValidationException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final RateLimitingService rateLimitingService;

    // --- Helper to Set HTTP Cookies ---
    private void setTokenCookies(HttpServletResponse response, String accessToken, String refreshToken) {
        // Refresh Token: HttpOnly, Secure, 7 Days
        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(false) // Set true in production (HTTPS)
                .sameSite("Strict")
                .path("/api/v1/auth/refresh-token") // Limit scope
                .maxAge(Duration.ofDays(7))
                .build();

        // Access Token: HttpOnly, Secure, 15 Minutes
        ResponseCookie accessCookie = ResponseCookie.from("access_token", accessToken)
                .httpOnly(true)
                .secure(false) // Set true in production
                .sameSite("Strict")
                .path("/")
                .maxAge(Duration.ofMinutes(15))
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request, HttpServletRequest servletRequest) {
        String ip = servletRequest.getRemoteAddr();

        if (!rateLimitingService.allowRegistration(ip)) {
            return ResponseEntity.status(429).body(Map.of("error", "Too many registration attempts. Please try again in an hour."));
        }

        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request,
                                   HttpServletRequest servletRequest,
                                   HttpServletResponse response) {
        String ip = servletRequest.getRemoteAddr();

        if (!rateLimitingService.allowLogin(ip)) {
            return ResponseEntity.status(429).body(Map.of("error", "Too many login attempts. Please wait 15 minutes."));
        }

        LoginResponse loginResponse = authService.login(request);

        // Set Cookies (for Browser/Swagger auto-pickup)
        setTokenCookies(response, loginResponse.getAccessToken(), loginResponse.getRefreshToken());

        // Return Tokens in Body (so you can see them to copy-paste if needed)
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("message", "Login successful");
        responseBody.put("expiresIn", loginResponse.getExpiresIn());
        responseBody.put("accessToken", loginResponse.getAccessToken()); // Added
        responseBody.put("refreshToken", loginResponse.getRefreshToken()); // Added

        return ResponseEntity.ok(responseBody);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(
            @CookieValue(name = "refresh_token", required = false) String refreshTokenFromCookie,
            @RequestBody(required = false) TokenRefreshRequest requestBody,
            HttpServletResponse response) {


        String token = refreshTokenFromCookie;
        if (token == null && requestBody != null) {
            token = requestBody.getRefreshToken();
        }

        if (token == null) {
            throw new ValidationException("Refresh token is required");
        }

        TokenRefreshResponse result = authService.refreshToken(token);

        // Rotate cookies
        setTokenCookies(response, result.getAccessToken(), result.getRefreshToken());

        return ResponseEntity.ok(Map.of("message", "Token refreshed successfully"));
    }

    @PostMapping("/forgot")
    public ResponseEntity<?> forgot(@RequestBody PasswordResetRequest request, HttpServletRequest servletRequest) {
        // Rate Limit Check
        if (!rateLimitingService.allowPasswordReset(servletRequest.getRemoteAddr())) {
            return ResponseEntity.status(429).body(Map.of("error", "Too many reset attempts. Please try again later."));
        }

        authService.initiatePasswordReset(request.getEmail());
        return ResponseEntity.ok(Map.of("message", "Reset email sent"));
    }

    // ... Other endpoints (verify-email, reset, logout) remain largely the same ...
    @PostMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestBody VerifyEmailRequest request) {
        return ResponseEntity.ok(authService.verifyEmail(request));
    }

    @PostMapping("/reset")
    public ResponseEntity<?> reset(@RequestBody PasswordResetDto request) {
        authService.resetPassword(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok(Map.of("message", "Password reset successful"));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody(required = false) TokenRefreshRequest request,
                                    @CookieValue(name = "refresh_token", required = false) String cookieToken,
                                    HttpServletResponse response) {
        String token = cookieToken != null ? cookieToken : (request != null ? request.getRefreshToken() : null);

        if (token != null) {
            authService.logout(token);
        }

        // Clear cookies
        ResponseCookie clearRefresh = ResponseCookie.from("refresh_token", "").maxAge(0).path("/api/v1/auth/refresh-token").build();
        ResponseCookie clearAccess = ResponseCookie.from("access_token", "").maxAge(0).path("/").build();

        response.addHeader(HttpHeaders.SET_COOKIE, clearRefresh.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, clearAccess.toString());

        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }
}