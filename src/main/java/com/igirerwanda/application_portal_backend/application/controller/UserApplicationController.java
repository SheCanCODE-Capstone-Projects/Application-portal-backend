package com.igirerwanda.application_portal_backend.application.controller;

import com.igirerwanda.application_portal_backend.application.dto.*;
import com.igirerwanda.application_portal_backend.application.service.UserApplicationService;
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

@RestController
@RequestMapping("/api/v1/user/applications")
@RequiredArgsConstructor
@Tag(name = "Application Process", description = "Endpoints for the multi-step application form")
public class UserApplicationController {

    private final UserApplicationService userApplicationService;
    private final JwtUtil jwtUtil;


    @PostMapping("/start")
    @Operation(summary = "Initialize Application", description = "Creates a DRAFT application for the user's selected cohort.")
    public ResponseEntity<ApiResponse<ApplicationDto>> startApplication() {
        Long userId = jwtUtil.getCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success(
                "Application started successfully",
                userApplicationService.startApplicationForUser(userId)
        ));
    }

    @GetMapping("/my-application")
    public ResponseEntity<ApiResponse<ApplicationDto>> getMyApplication() {
        Long userId = jwtUtil.getCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success(
                "Application retrieved",
                userApplicationService.startApplicationForUser(userId)
        ));
    }

    @PutMapping("/{id}/personal-info")
    public ResponseEntity<ApiResponse<ApplicationDto>> savePersonalInfo(
            @PathVariable Long id, @Valid @RequestBody PersonalInfoDto dto) {
        Long userId = jwtUtil.getCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success("Saved", userApplicationService.savePersonalInfo(id, userId, dto)));
    }

    @PutMapping("/{id}/education")
    public ResponseEntity<ApiResponse<ApplicationDto>> saveEducation(
            @PathVariable Long id, @Valid @RequestBody EducationDto dto) {
        Long userId = jwtUtil.getCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success("Saved", userApplicationService.saveEducation(id, userId, dto)));
    }

    @PutMapping("/{id}/motivation")
    public ResponseEntity<ApiResponse<ApplicationDto>> saveMotivation(
            @PathVariable Long id, @Valid @RequestBody MotivationDto dto) {
        Long userId = jwtUtil.getCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success("Saved", userApplicationService.saveMotivation(id, userId, dto)));
    }

    @PutMapping("/{id}/disability")
    public ResponseEntity<ApiResponse<ApplicationDto>> saveDisability(
            @PathVariable Long id, @Valid @RequestBody DisabilityDto dto) {
        Long userId = jwtUtil.getCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success("Saved", userApplicationService.saveDisability(id, userId, dto)));
    }

    @PutMapping("/{id}/vulnerability")
    public ResponseEntity<ApiResponse<ApplicationDto>> saveVulnerability(
            @PathVariable Long id, @Valid @RequestBody VulnerabilityDto dto) {
        Long userId = jwtUtil.getCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success("Saved", userApplicationService.saveVulnerability(id, userId, dto)));
    }


    @PutMapping("/{id}/documents")
    @Operation(summary = "Save Documents", description = "Updates the list of uploaded documents. Replaces existing list.")
    public ResponseEntity<ApiResponse<ApplicationDto>> saveDocuments(
            @PathVariable Long id,
            @Valid @RequestBody List<DocumentDto> dtos) {
        Long userId = jwtUtil.getCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success(
                "Documents saved successfully",
                userApplicationService.saveDocuments(id, userId, dtos)
        ));
    }


    @PutMapping("/{id}/emergency-contacts")
    @Operation(summary = "Save Emergency Contacts", description = "Updates the list of emergency contacts. Replaces existing list.")
    public ResponseEntity<ApiResponse<ApplicationDto>> saveEmergencyContacts(
            @PathVariable Long id,
            @Valid @RequestBody List<EmergencyContactDto> dtos) {
        Long userId = jwtUtil.getCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success(
                "Contacts saved successfully",
                userApplicationService.saveEmergencyContacts(id, userId, dtos)
        ));
    }

    @PutMapping("/{id}/submit")
    @Operation(summary = "Submit Application", description = "Finalizes the application. Changes status to SUBMITTED.")
    public ResponseEntity<ApiResponse<ApplicationDto>> submit(@PathVariable Long id) {
        Long userId = jwtUtil.getCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success(
                "Application submitted successfully",
                userApplicationService.submitApplication(id, userId)
        ));
    }

    @GetMapping("/{id}/progress")
    public ResponseEntity<ApiResponse<Map<String, Double>>> getProgress(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(
                "Progress calculated",
                Map.of("percentage", userApplicationService.calculateCompletionPercentage(id))
        ));
    }
}