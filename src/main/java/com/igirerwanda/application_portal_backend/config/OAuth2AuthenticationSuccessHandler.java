package com.igirerwanda.application_portal_backend.config;

import com.igirerwanda.application_portal_backend.auth.entity.Register;
import com.igirerwanda.application_portal_backend.auth.repository.RegisterRepository;
import com.igirerwanda.application_portal_backend.common.enums.AuthProvider;
import com.igirerwanda.application_portal_backend.common.enums.UserRole;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final RegisterRepository registerRepository;

    @Value("${app.oauth2.redirect-url:http://localhost:3000/auth/callback}")
    private String redirectUrl;

    public OAuth2AuthenticationSuccessHandler(
            JwtService jwtService,
            RegisterRepository registerRepository
    ) {
        this.jwtService = jwtService;
        this.registerRepository = registerRepository;
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

        if (email == null || googleId == null) {
            response.sendRedirect(
                    redirectUrl.replace("/callback", "/error") + "?message=Missing+required+user+info"
            );
            return;
        }


        Register user = registerRepository.findByEmail(email)
                .orElseGet(() -> {
                    Register newUser = new Register();
                    newUser.setEmail(email);
                    newUser.setUsername(name);
                    newUser.setGoogleId(googleId);
                    newUser.setVerified(true);
                    newUser.setProvider(AuthProvider.GOOGLE);
                    newUser.setRole(UserRole.APPLICANT);
                    return registerRepository.save(newUser);
                });

        String token = jwtService.generateAccessToken(user);

        response.sendRedirect(redirectUrl + "?token=" + token);
    }
}
