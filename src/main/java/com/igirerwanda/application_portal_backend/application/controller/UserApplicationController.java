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
    @Operation(summary = "Initialize Application", 
               description = "Creates a DRAFT application for the user's selected cohort. Returns existing draft if one already exists.")
    public ResponseEntity<ApiResponse<ApplicationDto>> startApplication() {
        Long userId = jwtUtil.getCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success(
                "Application started successfully",
                userApplicationService.startApplicationForUser(userId)
        ));
    }

    @GetMapping("/my-application")
    @Operation(summary = "Get My Application", 
               description = "Retrieves the current application with all saved step data for progress restoration.")
    public ResponseEntity<ApiResponse<ApplicationDto>> getMyApplication() {
        Long userId = jwtUtil.getCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success(
                "Application retrieved",
                userApplicationService.getApplicationForUser(userId)
        ));
    }

    @PutMapping("/{id}/personal-info")
    @Operation(summary = "Save Personal Information", 
               description = "Updates personal information step. Only works for DRAFT applications. Overwrites previous data.")
    public ResponseEntity<ApiResponse<ApplicationDto>> savePersonalInfo(
            @PathVariable Long id, @Valid @RequestBody PersonalInfoDto dto) {
        Long userId = jwtUtil.getCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success("Personal information saved", 
                userApplicationService.savePersonalInfo(id, userId, dto)));
    }

    @PutMapping("/{id}/education")
    @Operation(summary = "Save Education Information", 
               description = "Updates education step. Only works for DRAFT applications. Overwrites previous data.")
    public ResponseEntity<ApiResponse<ApplicationDto>> saveEducation(
            @PathVariable Long id, @Valid @RequestBody EducationDto dto) {
        Long userId = jwtUtil.getCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success("Education information saved", 
                userApplicationService.saveEducation(id, userId, dto)));
    }

    @PutMapping("/{id}/motivation")
    @Operation(summary = "Save Motivation Answers", 
               description = "Updates motivation step. Only works for DRAFT applications. Overwrites previous data.")
    public ResponseEntity<ApiResponse<ApplicationDto>> saveMotivation(
            @PathVariable Long id, @Valid @RequestBody MotivationDto dto) {
        Long userId = jwtUtil.getCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success("Motivation answers saved", 
                userApplicationService.saveMotivation(id, userId, dto)));
    }

    @PutMapping("/{id}/disability")
    @Operation(summary = "Save Disability Information", 
               description = "Updates disability step. Only works for DRAFT applications. Overwrites previous data.")
    public ResponseEntity<ApiResponse<ApplicationDto>> saveDisability(
            @PathVariable Long id, @Valid @RequestBody DisabilityDto dto) {
        Long userId = jwtUtil.getCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success("Disability information saved", 
                userApplicationService.saveDisability(id, userId, dto)));
    }

    @PutMapping("/{id}/vulnerability")
    @Operation(summary = "Save Vulnerability Information", 
               description = "Updates vulnerability step. Only works for DRAFT applications. Overwrites previous data.")
    public ResponseEntity<ApiResponse<ApplicationDto>> saveVulnerability(
            @PathVariable Long id, @Valid @RequestBody VulnerabilityDto dto) {
        Long userId = jwtUtil.getCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success("Vulnerability information saved", 
                userApplicationService.saveVulnerability(id, userId, dto)));
    }


    @PutMapping("/{id}/documents")
    @Operation(summary = "Save Documents", 
               description = "Updates the list of uploaded documents. Replaces existing list completely. Only works for DRAFT applications.")
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
    @Operation(summary = "Save Emergency Contacts", 
               description = "Updates the list of emergency contacts. Replaces existing list completely. Only works for DRAFT applications.")
    public ResponseEntity<ApiResponse<ApplicationDto>> saveEmergencyContacts(
            @PathVariable Long id,
            @Valid @RequestBody List<EmergencyContactDto> dtos) {
        Long userId = jwtUtil.getCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success(
                "Emergency contacts saved successfully",
                userApplicationService.saveEmergencyContacts(id, userId, dtos)
        ));
    }

    @PutMapping("/{id}/submit")
    @Operation(summary = "Submit Application", 
               description = "Finalizes the application. Changes status from DRAFT to SUBMITTED. Validates all required steps are complete.")
    public ResponseEntity<ApiResponse<ApplicationDto>> submit(@PathVariable Long id) {
        Long userId = jwtUtil.getCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success(
                "Application submitted successfully",
                userApplicationService.submitApplication(id, userId)
        ));
    }

    @GetMapping("/{id}/progress")
    @Operation(summary = "Get Application Progress", 
               description = "Calculates completion percentage based on completed steps. Updates in real-time.")
    public ResponseEntity<ApiResponse<Map<String, Double>>> getProgress(@PathVariable Long id) {
        Long userId = jwtUtil.getCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success(
                "Progress calculated",
                Map.of("percentage", userApplicationService.calculateCompletionPercentage(id, userId))
        ));
    }
}