package com.igirerwanda.application_portal_backend.application.controller;

import com.igirerwanda.application_portal_backend.application.dto.*;
import com.igirerwanda.application_portal_backend.application.service.UserApplicationService;
import com.igirerwanda.application_portal_backend.common.enums.ApplicationStatus;
import com.igirerwanda.application_portal_backend.common.util.ApiResponse;
import com.igirerwanda.application_portal_backend.common.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/user/applications")
@RequiredArgsConstructor
@Tag(name = "Application Process", description = "Endpoints for the multi-step application form")
public class UserApplicationController {

    private final UserApplicationService userApplicationService;
    private final JwtUtil jwtUtil;

    private UUID getUserId() {
        return UUID.fromString(jwtUtil.getCurrentUserId()); // Ensures String -> UUID conversion
    }

    @PostMapping("/start")
    @Operation(summary = "Initialize Application")
    public ResponseEntity<ApiResponse<ApplicationDto>> startApplication() {
        return ResponseEntity.ok(ApiResponse.success(
                "Application started successfully",
                userApplicationService.startApplicationForUser(getUserId())
        ));
    }

    @GetMapping("/my-application")
    @Operation(summary = "Get My Application")
    public ResponseEntity<ApiResponse<ApplicationDto>> getMyApplication() {
        return ResponseEntity.ok(ApiResponse.success(
                "Application retrieved",
                userApplicationService.getApplicationForUser(getUserId())
        ));
    }

    @PutMapping("/{id}/personal-info")
    public ResponseEntity<ApiResponse<ApplicationDto>> savePersonalInfo(
            @PathVariable UUID id, @Valid @RequestBody PersonalInfoDto dto) {
        return ResponseEntity.ok(ApiResponse.success("Personal information saved",
                userApplicationService.savePersonalInfo(id, getUserId(), dto)));
    }

    @PutMapping("/{id}/education")
    public ResponseEntity<ApiResponse<ApplicationDto>> saveEducation(
            @PathVariable UUID id, @Valid @RequestBody EducationDto dto) {
        return ResponseEntity.ok(ApiResponse.success("Education information saved",
                userApplicationService.saveEducation(id, getUserId(), dto)));
    }

    @PutMapping("/{id}/motivation")
    public ResponseEntity<ApiResponse<ApplicationDto>> saveMotivation(
            @PathVariable UUID id, @Valid @RequestBody MotivationDto dto) {
        return ResponseEntity.ok(ApiResponse.success("Motivation answers saved",
                userApplicationService.saveMotivation(id, getUserId(), dto)));
    }

    @PutMapping("/{id}/disability")
    public ResponseEntity<ApiResponse<ApplicationDto>> saveDisability(
            @PathVariable UUID id, @Valid @RequestBody DisabilityDto dto) {
        return ResponseEntity.ok(ApiResponse.success("Disability information saved",
                userApplicationService.saveDisability(id, getUserId(), dto)));
    }

    @PutMapping("/{id}/vulnerability")
    public ResponseEntity<ApiResponse<ApplicationDto>> saveVulnerability(
            @PathVariable UUID id, @Valid @RequestBody VulnerabilityDto dto) {
        return ResponseEntity.ok(ApiResponse.success("Vulnerability information saved",
                userApplicationService.saveVulnerability(id, getUserId(), dto)));
    }

    @PutMapping("/{id}/documents")
    public ResponseEntity<ApiResponse<ApplicationDto>> saveDocuments(
            @PathVariable UUID id, @Valid @RequestBody List<DocumentDto> dtos) {
        return ResponseEntity.ok(ApiResponse.success(
                "Documents saved successfully",
                userApplicationService.saveDocuments(id, getUserId(), dtos)
        ));
    }

    @PutMapping("/{id}/emergency-contacts")
    public ResponseEntity<ApiResponse<ApplicationDto>> saveEmergencyContacts(
            @PathVariable UUID id, @Valid @RequestBody List<EmergencyContactDto> dtos) {
        return ResponseEntity.ok(ApiResponse.success(
                "Emergency contacts saved successfully",
                userApplicationService.saveEmergencyContacts(id, getUserId(), dtos)
        ));
    }

    @PutMapping("/{id}/submit")
    @Operation(summary = "Submit Application")
    public ResponseEntity<ApiResponse<ApplicationSubmissionResponseDto>> submit(@PathVariable UUID id) {
        ApplicationSubmissionResponseDto response = userApplicationService.submitApplication(id, getUserId());

        String message = response.getStatus() == ApplicationStatus.SYSTEM_REJECTED
                ? "Application automatically rejected: " + response.getRejectionReason()
                : "Application submitted successfully";

        return ResponseEntity.ok(ApiResponse.success(message, response));
    }

    @GetMapping("/{id}/progress")
    @Operation(summary = "Get Application Progress")
    public ResponseEntity<ApiResponse<Map<String, Double>>> getProgress(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(
                "Progress calculated",
                Map.of("percentage", userApplicationService.calculateCompletionPercentage(id, getUserId()))
        ));
    }
}