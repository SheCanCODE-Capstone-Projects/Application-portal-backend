package com.igirerwanda.application_portal_backend.application.security;

import com.igirerwanda.application_portal_backend.application.dto.ApplicationDto;
import com.igirerwanda.application_portal_backend.application.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ApplicationSecurityService {

    private final ApplicationService applicationService;

    public Long getUserIdFromAuth(Authentication auth) {
        String email = auth.getName();
        // TODO: Implement proper user ID extraction from JWT claims
        return 1L; // Placeholder
    }

    public void validateUserOwnsApplication(Long applicationId, Authentication auth) {
        Long userId = getUserIdFromAuth(auth);
        ApplicationDto application = applicationService.getApplication(applicationId);
        
        if (!application.getUserId().equals(userId)) {
            throw new SecurityException("Access denied: You can only access your own applications");
        }
    }
}