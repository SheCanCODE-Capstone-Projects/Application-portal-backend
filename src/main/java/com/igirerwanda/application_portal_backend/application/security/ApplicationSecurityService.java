package com.igirerwanda.application_portal_backend.application.security;

import com.igirerwanda.application_portal_backend.application.repository.ApplicationRepository;
import com.igirerwanda.application_portal_backend.common.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ApplicationSecurityService {
    private final ApplicationRepository applicationRepository;
    private final JwtUtil jwtUtil;

    public boolean isOwner(UUID applicationId) {
        // Convert the String from JWT to UUID before comparison
        UUID currentUserId = UUID.fromString(jwtUtil.getCurrentUserId());

        return applicationRepository.findById(applicationId)
                .map(app -> app.getUser().getId().equals(currentUserId))
                .orElse(false);
    }
}