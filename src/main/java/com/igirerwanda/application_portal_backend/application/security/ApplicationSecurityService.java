package com.igirerwanda.application_portal_backend.application.security;

import com.igirerwanda.application_portal_backend.application.entity.Application;
import com.igirerwanda.application_portal_backend.application.repository.ApplicationRepository;
import com.igirerwanda.application_portal_backend.common.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ApplicationSecurityService {
    private final ApplicationRepository applicationRepository;
    private final JwtUtil jwtUtil;

    public boolean isOwner(Long applicationId) {
        Long currentUserId = jwtUtil.getCurrentUserId();
        return applicationRepository.findById(applicationId)
                .map(app -> app.getUser().getId().equals(currentUserId))
                .orElse(false);
    }
}