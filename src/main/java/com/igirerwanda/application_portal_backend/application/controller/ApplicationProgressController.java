package com.igirerwanda.application_portal_backend.application.controller;

import com.igirerwanda.application_portal_backend.application.dto.*;
import com.igirerwanda.application_portal_backend.application.service.ApplicationProgressiveService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/applications/progress")
@RequiredArgsConstructor
public class ApplicationProgressController {

    private final ApplicationProgressiveService progressiveService;

    @PutMapping("/{applicationId}/personal-info")
    public ResponseEntity<ApplicationDto> savePersonalInfoStep(
            @PathVariable Long applicationId,
            @Valid @RequestBody PersonalInfoDto dto) {
        ApplicationDto result = progressiveService.savePersonalInfoStep(applicationId, dto);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{applicationId}/education")
    public ResponseEntity<ApplicationDto> saveEducationStep(
            @PathVariable Long applicationId,
            @Valid @RequestBody EducationDto dto) {
        ApplicationDto result = progressiveService.saveEducationStep(applicationId, dto);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{applicationId}/motivation")
    public ResponseEntity<ApplicationDto> saveMotivationStep(
            @PathVariable Long applicationId,
            @Valid @RequestBody MotivationDto dto) {
        ApplicationDto result = progressiveService.saveMotivationStep(applicationId, dto);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{applicationId}/disability")
    public ResponseEntity<ApplicationDto> saveDisabilityStep(
            @PathVariable Long applicationId,
            @Valid @RequestBody DisabilityDto dto) {
        ApplicationDto result = progressiveService.saveDisabilityStep(applicationId, dto);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{applicationId}/vulnerability")
    public ResponseEntity<ApplicationDto> saveVulnerabilityStep(
            @PathVariable Long applicationId,
            @Valid @RequestBody VulnerabilityDto dto) {
        ApplicationDto result = progressiveService.saveVulnerabilityStep(applicationId, dto);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{applicationId}")
    public ResponseEntity<ApplicationDto> getApplicationProgress(@PathVariable Long applicationId) {
        ApplicationDto result = progressiveService.getApplicationProgress(applicationId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{applicationId}/completion-percentage")
    public ResponseEntity<Map<String, Double>> getCompletionPercentage(@PathVariable Long applicationId) {
        double percentage = progressiveService.calculateCompletionPercentage(applicationId);
        return ResponseEntity.ok(Map.of("completionPercentage", percentage));
    }
}