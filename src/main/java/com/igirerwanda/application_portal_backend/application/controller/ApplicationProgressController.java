package com.igirerwanda.application_portal_backend.application.controller;

import com.igirerwanda.application_portal_backend.application.dto.*;
import com.igirerwanda.application_portal_backend.application.service.ApplicationProgressiveService;
import com.igirerwanda.application_portal_backend.application.service.ApplicationService;
import com.igirerwanda.application_portal_backend.common.util.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/application-progress-controller")
@CrossOrigin(origins = "*")
public class ApplicationProgressController {

    private final ApplicationProgressiveService progressiveService;
    private final ApplicationService applicationService;
    private final JwtUtil jwtUtil;

    @PutMapping("/{applicationId}/personal-info")
    public ResponseEntity<ApplicationDto> savePersonalInfoStep(
            @PathVariable Long applicationId,
            @Valid @RequestBody PersonalInfoDto dto,
            Authentication auth) {
        validateUserAccess(applicationId, auth);
        ApplicationDto result = progressiveService.savePersonalInfoStep(applicationId, dto);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{applicationId}/education")
    public ResponseEntity<ApplicationDto> saveEducationStep(
            @PathVariable Long applicationId,
            @Valid @RequestBody EducationDto dto,
            Authentication auth) {
        validateUserAccess(applicationId, auth);
        ApplicationDto result = progressiveService.saveEducationStep(applicationId, dto);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{applicationId}/motivation")
    public ResponseEntity<ApplicationDto> saveMotivationStep(
            @PathVariable Long applicationId,
            @Valid @RequestBody MotivationDto dto,
            Authentication auth) {
        validateUserAccess(applicationId, auth);
        ApplicationDto result = progressiveService.saveMotivationStep(applicationId, dto);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{applicationId}/disability")
    public ResponseEntity<ApplicationDto> saveDisabilityStep(
            @PathVariable Long applicationId,
            @Valid @RequestBody DisabilityDto dto,
            Authentication auth) {
        validateUserAccess(applicationId, auth);
        ApplicationDto result = progressiveService.saveDisabilityStep(applicationId, dto);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{applicationId}/vulnerability")
    public ResponseEntity<ApplicationDto> saveVulnerabilityStep(
            @PathVariable Long applicationId,
            @Valid @RequestBody VulnerabilityDto dto,
            Authentication auth) {
        validateUserAccess(applicationId, auth);
        ApplicationDto result = progressiveService.saveVulnerabilityStep(applicationId, dto);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{applicationId}/submit")
    public ResponseEntity<ApplicationDto> submitApplication(
            @PathVariable Long applicationId,
            Authentication auth) {
        validateUserAccess(applicationId, auth);
        ApplicationDto result = applicationService.submitApplication(applicationId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{applicationId}")
    public ResponseEntity<ApplicationDto> getApplicationProgress(
            @PathVariable Long applicationId,
            Authentication auth) {
        validateUserAccess(applicationId, auth);
        ApplicationDto result = progressiveService.getApplicationProgress(applicationId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{applicationId}/completion-percentage")
    public ResponseEntity<Map<String, Double>> getCompletionPercentage(
            @PathVariable Long applicationId,
            Authentication auth) {
        validateUserAccess(applicationId, auth);
        double percentage = progressiveService.calculateCompletionPercentage(applicationId);
        return ResponseEntity.ok(Map.of("completionPercentage", percentage));
    }

    private void validateUserAccess(Long applicationId, Authentication auth) {
        Long userId = jwtUtil.getCurrentUserId();
        ApplicationDto application = applicationService.getApplication(applicationId);
        
        if (!application.getUserId().equals(userId)) {
            throw new SecurityException("Access denied: You can only access your own applications");
        }
    }
}