package com.igirerwanda.application_portal_backend.auth.service;

import com.igirerwanda.application_portal_backend.auth.dto.LoginResponse;
import com.igirerwanda.application_portal_backend.auth.entity.RefreshToken;
import com.igirerwanda.application_portal_backend.auth.entity.Register;
import com.igirerwanda.application_portal_backend.auth.repository.RegisterRepository;
import com.igirerwanda.application_portal_backend.common.enums.AuthProvider;
import com.igirerwanda.application_portal_backend.common.enums.UserRole;
import com.igirerwanda.application_portal_backend.config.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GoogleAuthService {

    private final RegisterRepository registerRepository;
    private final JwtService jwtService;
    private final UserPromotionService userPromotionService;
    private final RefreshTokenService refreshTokenService;
    
    @Value("${jwt.access.token.expiration:900000}")
    private long accessTokenExpiration;

    @Transactional
    public LoginResponse handleGoogleAuth(String email, String googleId, String name) {
        Register register = registerRepository.findByEmail(email)
                .map(existing -> {

                    if (existing.getProvider() != AuthProvider.GOOGLE && existing.getProvider() != AuthProvider.LOCAL) {
                        throw new IllegalStateException("Email already registered with another provider");
                    }

                    if (existing.getProvider() == AuthProvider.LOCAL) {
                        existing.setProvider(AuthProvider.GOOGLE);
                        existing.setGoogleId(googleId);
                        existing.setVerified(true);
                        return registerRepository.save(existing);
                    }
                    return existing;
                })
                .orElseGet(() -> {
                    Register newUser = new Register();
                    newUser.setEmail(email);
                    newUser.setGoogleId(googleId);
                    newUser.setUsername(name);
                    newUser.setVerified(true);
                    newUser.setProvider(AuthProvider.GOOGLE);
                    newUser.setRole(UserRole.APPLICANT);
                    return registerRepository.save(newUser);
                });


        userPromotionService.promote(register);
        

        String accessToken = jwtService.generateAccessToken(register);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(register);
        
        return new LoginResponse(accessToken, refreshToken.getToken(), accessTokenExpiration);
    }
    
    public String handleGoogleLogin(String email, String googleId, String name) {
        LoginResponse response = handleGoogleAuth(email, googleId, name);
        return response.getAccessToken();
    }
}