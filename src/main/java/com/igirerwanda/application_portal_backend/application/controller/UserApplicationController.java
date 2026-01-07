package com.igirerwanda.application_portal_backend.application.controller;

import com.igirerwanda.application_portal_backend.application.dto.*;
import com.igirerwanda.application_portal_backend.application.service.UserApplicationService;
import com.igirerwanda.application_portal_backend.common.util.ApiResponse;
import com.igirerwanda.application_portal_backend.common.util.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/user/applications")
@RequiredArgsConstructor
public class UserApplicationController {

    private final UserApplicationService userApplicationService;
    private final JwtUtil jwtUtil;


    @PostMapping("/start")
    public ResponseEntity<ApiResponse<ApplicationDto>> startApplication() {
        Long userId = jwtUtil.getCurrentUserId();
        ApplicationDto app = userApplicationService.startApplicationForUser(userId);
        return ResponseEntity.ok(ApiResponse.success("Application started successfully", app));
    }


    @GetMapping("/my-application")
    public ResponseEntity<ApiResponse<ApplicationDto>> getMyApplication() {
        Long userId = jwtUtil.getCurrentUserId();
        ApplicationDto app = userApplicationService.startApplicationForUser(userId);
        return ResponseEntity.ok(ApiResponse.success("Application retrieved", app));
    }



    @PutMapping("/{id}/personal-info")
    public ResponseEntity<ApiResponse<ApplicationDto>> savePersonalInfo(
            @PathVariable Long id,
            @Valid @RequestBody PersonalInfoDto dto) {
        Long userId = jwtUtil.getCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success(
                "Personal info saved",
                userApplicationService.savePersonalInfo(id, userId, dto)
        ));
    }

    @PutMapping("/{id}/education")
    public ResponseEntity<ApiResponse<ApplicationDto>> saveEducation(
            @PathVariable Long id,
            @Valid @RequestBody EducationDto dto) {
        Long userId = jwtUtil.getCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success(
                "Education info saved",
                userApplicationService.saveEducation(id, userId, dto)
        ));
    }

    @PutMapping("/{id}/motivation")
    public ResponseEntity<ApiResponse<ApplicationDto>> saveMotivation(
            @PathVariable Long id,
            @Valid @RequestBody MotivationDto dto) {
        Long userId = jwtUtil.getCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success(
                "Motivation saved",
                userApplicationService.saveMotivation(id, userId, dto)
        ));
    }

    @PutMapping("/{id}/disability")
    public ResponseEntity<ApiResponse<ApplicationDto>> saveDisability(
            @PathVariable Long id,
            @Valid @RequestBody DisabilityDto dto) {
        Long userId = jwtUtil.getCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success(
                "Disability info saved",
                userApplicationService.saveDisability(id, userId, dto)
        ));
    }

    @PutMapping("/{id}/vulnerability")
    public ResponseEntity<ApiResponse<ApplicationDto>> saveVulnerability(
            @PathVariable Long id,
            @Valid @RequestBody VulnerabilityDto dto) {
        Long userId = jwtUtil.getCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success(
                "Vulnerability info saved",
                userApplicationService.saveVulnerability(id, userId, dto)
        ));
    }



    @PutMapping("/{id}/submit")
    public ResponseEntity<ApiResponse<ApplicationDto>> submit(@PathVariable Long id) {
        Long userId = jwtUtil.getCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success(
                "Application submitted successfully",
                userApplicationService.submitApplication(id, userId)
        ));
    }

    @GetMapping("/{id}/progress")
    public ResponseEntity<ApiResponse<Map<String, Double>>> getProgress(@PathVariable Long id) {
        double percentage = userApplicationService.calculateCompletionPercentage(id);
        return ResponseEntity.ok(ApiResponse.success(
                "Progress calculated",
                Map.of("percentage", percentage)
        ));
    }
}