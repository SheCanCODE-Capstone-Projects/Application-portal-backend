package com.igirerwanda.application_portal_backend.config;

import com.igirerwanda.application_portal_backend.auth.service.GoogleAuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final GoogleAuthService googleAuthService;


    @Value("${app.frontend.base-url}")
    private String frontendBaseUrl;

    public OAuth2AuthenticationSuccessHandler(GoogleAuthService googleAuthService) {
        this.googleAuthService = googleAuthService;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {

        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();

        String email = oauthUser.getAttribute("email");
        String name = oauthUser.getAttribute("name");
        String googleId = oauthUser.getAttribute("sub");


        String baseUrl = frontendBaseUrl.endsWith("/") ? frontendBaseUrl.substring(0, frontendBaseUrl.length() - 1) : frontendBaseUrl;
        String targetUrl = baseUrl + "/auth/callback";
        String errorUrl = baseUrl + "/auth/error";

        if (email == null || googleId == null) {
            response.sendRedirect(errorUrl + "?message=Missing+required+user+info");
            return;
        }

        try {
            String token = googleAuthService.handleGoogleLogin(email, googleId, name);

            response.sendRedirect(targetUrl + "?token=" + token);

        } catch (Exception e) {
            String errorMsg = URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
            response.sendRedirect(errorUrl + "?message=" + errorMsg);
        }
    }
}