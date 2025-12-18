package com.igirerwanda.application_portal_backend.auth.service;

import com.igirerwanda.application_portal_backend.auth.entity.Register;
import com.igirerwanda.application_portal_backend.auth.repository.RegisterRepository;
import com.igirerwanda.application_portal_backend.common.enums.AuthProvider;
import com.igirerwanda.application_portal_backend.common.enums.UserRole;
import com.igirerwanda.application_portal_backend.config.JwtService;
import org.springframework.stereotype.Service;

@Service
public class GoogleAuthService {

    private final RegisterRepository registerRepository;
    private final JwtService jwtService;

    public GoogleAuthService(RegisterRepository registerRepository,
                             JwtService jwtService) {
        this.registerRepository = registerRepository;
        this.jwtService = jwtService;
    }

    public String handleGoogleLogin(String email, String googleId, String name) {

        Register user = registerRepository.findByEmail(email)
                .map(existing -> {
                    if (existing.getProvider() != AuthProvider.GOOGLE) {
                        throw new IllegalStateException(
                                "Email already registered with another provider"
                        );
                    }

                    if (existing.getGoogleId() != null &&
                            !existing.getGoogleId().equals(googleId)) {
                        throw new IllegalStateException("Google account mismatch");
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

        return jwtService.generateAccessToken(user);
    }
}
