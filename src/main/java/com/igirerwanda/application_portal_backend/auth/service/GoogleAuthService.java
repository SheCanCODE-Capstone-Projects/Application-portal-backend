package com.igirerwanda.application_portal_backend.auth.service;

import com.igirerwanda.application_portal_backend.auth.entity.Register;
import com.igirerwanda.application_portal_backend.auth.repository.RegisterRepository;
import com.igirerwanda.application_portal_backend.common.enums.AuthProvider;
import com.igirerwanda.application_portal_backend.common.enums.UserRole;
import com.igirerwanda.application_portal_backend.config.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GoogleAuthService {

    private final RegisterRepository registerRepository;
    private final JwtService jwtService;
    private final UserPromotionService userPromotionService;

    @Transactional
    public String handleGoogleLogin(String email, String googleId, String name) {

        Register register = registerRepository.findByEmail(email)
                .map(existing -> {
                    if (existing.getProvider() != AuthProvider.GOOGLE) {
                        throw new IllegalStateException("Email already registered with another provider");
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

        // ✅ CRITICAL: This ensures the record is created in the 'users' table
        userPromotionService.promote(register);

        return jwtService.generateAccessToken(register);
    }
}