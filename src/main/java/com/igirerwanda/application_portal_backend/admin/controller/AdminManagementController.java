package com.igirerwanda.application_portal_backend.admin.controller;

import com.igirerwanda.application_portal_backend.admin.dto.AdminCreateDto;
import com.igirerwanda.application_portal_backend.admin.dto.AdminResponseDto;
import com.igirerwanda.application_portal_backend.admin.entity.AdminActivity;
import com.igirerwanda.application_portal_backend.admin.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/management")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin Management", description = "Admin user management operations")
public class AdminManagementController {

    private final AdminService adminService;

    @PostMapping
    @Operation(summary = "Create new admin user")
    public ResponseEntity<AdminResponseDto> createAdmin(@Valid @RequestBody AdminCreateDto adminCreateDto) {
        AdminResponseDto response = adminService.createAdmin(adminCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Get all admin users")
    public ResponseEntity<List<AdminResponseDto>> getAllAdmins() {
        List<AdminResponseDto> admins = adminService.getAllAdmins();
        return ResponseEntity.ok(admins);
    }

    @GetMapping("/{adminId}")
    @Operation(summary = "Get admin user by ID")
    public ResponseEntity<AdminResponseDto> getAdminById(@PathVariable Long adminId) {
        AdminResponseDto admin = adminService.getAdminById(adminId);
        return ResponseEntity.ok(admin);
    }

    @PutMapping("/{adminId}")
    @Operation(summary = "Update admin user")
    public ResponseEntity<AdminResponseDto> updateAdmin(
            @PathVariable Long adminId,
            @Valid @RequestBody AdminCreateDto adminUpdateDto) {
        AdminResponseDto response = adminService.updateAdmin(adminId, adminUpdateDto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{adminId}")
    @Operation(summary = "Delete admin user")
    public ResponseEntity<Void> deleteAdmin(@PathVariable Long adminId) {
        adminService.deleteAdmin(adminId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/activities")
    @Operation(summary = "Get all admin activities")
    public ResponseEntity<List<AdminActivity>> getAdminActivities() {
        List<AdminActivity> activities = adminService.getAdminActivities();
        return ResponseEntity.ok(activities);
    }
}