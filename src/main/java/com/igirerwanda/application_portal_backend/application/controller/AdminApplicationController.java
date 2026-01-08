package com.igirerwanda.application_portal_backend.application.controller;

import com.igirerwanda.application_portal_backend.application.dto.ApplicationDto;
import com.igirerwanda.application_portal_backend.application.dto.InterviewScheduleRequest;
import com.igirerwanda.application_portal_backend.application.service.AdminApplicationService;
import com.igirerwanda.application_portal_backend.common.enums.ApplicationStatus;
import com.igirerwanda.application_portal_backend.common.util.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/applications")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin Application Management", description = "Endpoints for managing applications")
public class AdminApplicationController {

    private final AdminApplicationService adminService;

    // --- Retrieval Endpoints ---

    @GetMapping
    @Operation(summary = "Get All Active", description = "Get all active (non-deleted, non-archived) applications")
    public ResponseEntity<ApiResponse<List<ApplicationDto>>> getAllApplications() {
        return ResponseEntity.ok(ApiResponse.success(
                "All active applications retrieved",
                adminService.getAllActiveApplications()
        ));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Application By ID", description = "Get details of any application (active, deleted, archived, rejected)")
    public ResponseEntity<ApiResponse<ApplicationDto>> getDetails(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(
                "Application details retrieved",
                adminService.getApplicationDetails(id)
        ));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get By Status", description = "Get applications filtered by specific status")
    public ResponseEntity<ApiResponse<List<ApplicationDto>>> getByStatus(@PathVariable ApplicationStatus status) {
        return ResponseEntity.ok(ApiResponse.success(
                "Applications retrieved by status",
                adminService.getApplicationsByStatus(status)
        ));
    }

    @GetMapping("/system-rejected")
    @Operation(summary = "Get System Rejected", description = "Get all applications rejected by the system rules")
    public ResponseEntity<ApiResponse<List<ApplicationDto>>> getSystemRejected() {
        return ResponseEntity.ok(ApiResponse.success(
                "System rejected applications retrieved",
                adminService.getSystemRejectedApplications()
        ));
    }

    @GetMapping("/archived")
    @Operation(summary = "Get Archived", description = "Get all archived applications")
    public ResponseEntity<ApiResponse<List<ApplicationDto>>> getArchived() {
        return ResponseEntity.ok(ApiResponse.success(
                "Archived applications retrieved",
                adminService.getArchivedApplications()
        ));
    }

    @GetMapping("/deleted")
    @Operation(summary = "Get Deleted", description = "Get all soft-deleted applications")
    public ResponseEntity<ApiResponse<List<ApplicationDto>>> getDeleted() {
        return ResponseEntity.ok(ApiResponse.success(
                "Deleted applications retrieved",
                adminService.getDeletedApplications()
        ));
    }

    // --- Workflow Action Endpoints (PUT by ID) ---

    @PutMapping("/{id}/accept")
    @Operation(summary = "Accept Application", description = "Approves application and sends notification emails.")
    public ResponseEntity<ApiResponse<ApplicationDto>> accept(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(
                "Application accepted and applicant notified",
                adminService.acceptApplication(id)
        ));
    }

    @PutMapping("/{id}/reject")
    @Operation(summary = "Reject Application", description = "Rejects application and sends notification emails.")
    public ResponseEntity<ApiResponse<ApplicationDto>> reject(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(
                "Application rejected and applicant notified",
                adminService.rejectApplication(id)
        ));
    }

    @PutMapping("/{id}/schedule-interview")
    @Operation(summary = "Schedule Interview", description = "Sets interview date/time and notifies applicant with instructions.")
    public ResponseEntity<ApiResponse<ApplicationDto>> scheduleInterview(
            @PathVariable Long id,
            @Valid @RequestBody InterviewScheduleRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                "Interview scheduled and invitation sent",
                adminService.scheduleInterview(id, request)
        ));
    }

    // --- Management Action Endpoints ---

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft Delete", description = "Soft deletes an application.")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        adminService.softDeleteApplication(id);
        return ResponseEntity.ok(ApiResponse.success("Application soft deleted successfully"));
    }

    @PutMapping("/{id}/archive")
    @Operation(summary = "Archive", description = "Moves application to archive.")
    public ResponseEntity<ApiResponse<Void>> archive(@PathVariable Long id) {
        adminService.archiveApplication(id);
        return ResponseEntity.ok(ApiResponse.success("Application archived successfully"));
    }

    @PutMapping("/{id}/restore")
    @Operation(summary = "Restore", description = "Restores a deleted or archived application.")
    public ResponseEntity<ApiResponse<Void>> restore(@PathVariable Long id) {
        adminService.restoreApplication(id);
        return ResponseEntity.ok(ApiResponse.success("Application restored successfully"));
    }
}