package com.igirerwanda.application_portal_backend.application.controller;

import com.igirerwanda.application_portal_backend.application.dto.ApplicationDto;
import com.igirerwanda.application_portal_backend.application.dto.ApplicationStatusDto;
import com.igirerwanda.application_portal_backend.application.service.AdminApplicationService;
import com.igirerwanda.application_portal_backend.common.enums.ApplicationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/applications")
@RequiredArgsConstructor
// @PreAuthorize("hasRole('ADMIN')") // Ensure you enable security here
public class AdminApplicationController {

    private final AdminApplicationService adminService;

    @GetMapping("/status/{status}")
    public ResponseEntity<List<ApplicationDto>> getByStatus(@PathVariable ApplicationStatus status) {
        return ResponseEntity.ok(adminService.getApplicationsByStatus(status));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApplicationDto> getDetails(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.getApplicationDetails(id));
    }

    @PutMapping("/{id}/accept")
    public ResponseEntity<ApplicationDto> accept(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.updateStatus(id, ApplicationStatus.ACCEPTED));
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<ApplicationDto> reject(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.updateStatus(id, ApplicationStatus.REJECTED));
    }

    @PutMapping("/{id}/schedule-interview")
    public ResponseEntity<ApplicationDto> scheduleInterview(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(adminService.scheduleInterview(id, body.get("interviewDetails")));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApplicationDto> updateStatus(@PathVariable Long id, @RequestBody ApplicationStatusDto dto) {
        return ResponseEntity.ok(adminService.updateStatus(id, dto.getStatus()));
    }
}