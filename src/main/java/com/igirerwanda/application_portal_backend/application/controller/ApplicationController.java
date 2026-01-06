package com.igirerwanda.application_portal_backend.application.controller;

import com.igirerwanda.application_portal_backend.application.dto.*;
import com.igirerwanda.application_portal_backend.application.service.ApplicationService;
import com.igirerwanda.application_portal_backend.common.enums.ApplicationStatus;
import com.igirerwanda.application_portal_backend.common.util.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/application-controller")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;
    private final JwtUtil jwtUtil;

    @PostMapping
    public ResponseEntity<ApplicationDto> createApplication(
            @Valid @RequestBody ApplicationCreateDto dto,
            Authentication auth) {
        Long userId = getUserIdFromAuth(auth);
        ApplicationDto result = applicationService.createApplication(userId, dto);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/submit-complete")
    public ResponseEntity<ApplicationDto> submitCompleteApplication(
            @Valid @RequestBody ApplicationSubmissionDto dto,
            Authentication auth) {
        Long userId = getUserIdFromAuth(auth);
        ApplicationDto result = applicationService.submitCompleteApplication(userId, dto);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApplicationDto> getApplication(
            @PathVariable Long id, 
            Authentication auth) {
        Long userId = getUserIdFromAuth(auth);
        ApplicationDto result = applicationService.getApplication(id);
        
        // Authorization check: user can only access their own applications
        if (!result.getUserId().equals(userId)) {
            throw new SecurityException("Access denied: You can only access your own applications");
        }
        
        return ResponseEntity.ok(result);
    }

    @GetMapping("/getMyApplications")
    public ResponseEntity<List<ApplicationDto>> getMyApplications(Authentication auth) {
        Long userId = getUserIdFromAuth(auth);
        List<ApplicationDto> result = applicationService.getUserApplications(userId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<ApplicationDto>> getApplicationsByStatus(
            @PathVariable ApplicationStatus status) {
        List<ApplicationDto> result = applicationService.getApplicationsByStatus(status);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}/submit")
    public ResponseEntity<ApplicationDto> submitApplication(@PathVariable Long id) {
        ApplicationDto result = applicationService.submitApplication(id);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}/personal-info")
    public ResponseEntity<ApplicationDto> updatePersonalInfo(
            @PathVariable Long id,
            @Valid @RequestBody PersonalInfoDto dto,
            Authentication auth) {
        Long userId = getUserIdFromAuth(auth);
        
        // Authorization check: user can only update their own applications
        ApplicationDto existing = applicationService.getApplication(id);
        if (!existing.getUserId().equals(userId)) {
            throw new SecurityException("Access denied: You can only update your own applications");
        }
        
        ApplicationDto result = applicationService.updatePersonalInfo(id, dto);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}/education")
    public ResponseEntity<ApplicationDto> updateEducation(
            @PathVariable Long id,
            @RequestBody EducationDto dto) {
        ApplicationDto result = applicationService.updateEducation(id, dto);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}/motivation")
    public ResponseEntity<ApplicationDto> updateMotivation(
            @PathVariable Long id,
            @RequestBody MotivationDto dto) {
        ApplicationDto result = applicationService.updateMotivation(id, dto);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}/disability")
    public ResponseEntity<ApplicationDto> updateDisability(
            @PathVariable Long id,
            @RequestBody DisabilityDto dto) {
        ApplicationDto result = applicationService.updateDisability(id, dto);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}/vulnerability")
    public ResponseEntity<ApplicationDto> updateVulnerability(
            @PathVariable Long id,
            @RequestBody VulnerabilityDto dto) {
        ApplicationDto result = applicationService.updateVulnerability(id, dto);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{id}/documents")
    public ResponseEntity<ApplicationDto> addDocument(
            @PathVariable Long id,
            @RequestBody DocumentDto dto) {
        ApplicationDto result = applicationService.addDocument(id, dto);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{id}/emergency-contacts")
    public ResponseEntity<ApplicationDto> addEmergencyContact(
            @PathVariable Long id,
            @RequestBody EmergencyContactDto dto) {
        ApplicationDto result = applicationService.addEmergencyContact(id, dto);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/documents/{documentId}")
    public ResponseEntity<Map<String, String>> deleteDocument(@PathVariable Long documentId) {
        applicationService.deleteDocument(documentId);
        return ResponseEntity.ok(Map.of("message", "Document deleted successfully"));
    }

    @DeleteMapping("/emergency-contacts/{contactId}")
    public ResponseEntity<Map<String, String>> deleteEmergencyContact(@PathVariable Long contactId) {
        applicationService.deleteEmergencyContact(contactId);
        return ResponseEntity.ok(Map.of("message", "Emergency contact deleted successfully"));
    }

    @GetMapping("/{id}/completion-status")
    public ResponseEntity<Map<String, Boolean>> getCompletionStatus(@PathVariable Long id) {
        boolean isComplete = applicationService.isApplicationComplete(id);
        return ResponseEntity.ok(Map.of("isComplete", isComplete));
    }

    private Long getUserIdFromAuth(Authentication auth) {
        // Extract user ID from JWT token in request header
        return jwtUtil.getCurrentUserId();
    }
}
