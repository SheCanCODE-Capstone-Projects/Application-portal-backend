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
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final RegisterRepository registerRepository;
    private final JwtService jwtService;
    
    @Value("${app.oauth2.redirect-url:http://localhost:3000/auth/callback}")
    private String redirectUrl;

    public OAuth2AuthenticationSuccessHandler(RegisterRepository registerRepository, JwtService jwtService) {
        this.registerRepository = registerRepository;
        this.jwtService = jwtService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        
        String email = oAuth2User.getAttribute("email");
        String googleId = oAuth2User.getAttribute("sub");
        String name = oAuth2User.getAttribute("name");

        Register user = registerRepository.findByEmail(email)
                .orElseGet(() -> createNewUser(email, googleId, name));

        String token = jwtService.generateAccessToken(user);
        
        // Redirect to frontend with token only (role is in JWT claims)
        String finalRedirectUrl = redirectUrl + "?token=" + token;
        response.sendRedirect(finalRedirectUrl);
    }

    private Register createNewUser(String email, String googleId, String name) {
        Register user = new Register();
        user.setEmail(email);
        user.setGoogleId(googleId);
        user.setUsername(name);
        user.setVerified(true);
        user.setProvider(AuthProvider.GOOGLE);
        user.setRole(UserRole.APPLICANT);
        
        return registerRepository.save(user);
    }
}