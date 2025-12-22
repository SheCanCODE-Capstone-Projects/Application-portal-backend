package com.igirerwanda.application_portal_backend.admin.controller;

import com.igirerwanda.application_portal_backend.admin.dto.AdminCreateDto;
import com.igirerwanda.application_portal_backend.admin.dto.AdminResponseDto;
import com.igirerwanda.application_portal_backend.admin.dto.AdminActivityResponseDto;
import com.igirerwanda.application_portal_backend.admin.dto.ApiResponse;
import com.igirerwanda.application_portal_backend.admin.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {
    
    @Autowired
    private AdminService adminService;

    @Operation(summary = "Admin create User", description = "Admin create  User")
    @PostMapping("/admins_users")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<AdminResponseDto>> createUsers(@Valid @RequestBody AdminCreateDto adminCreateDto) {
        AdminResponseDto response = adminService.createAdmin(adminCreateDto);
        return new ResponseEntity<>(ApiResponse.success("User created successfully!", response), HttpStatus.CREATED);
    }

    @Operation(summary = "Get all activities", description = "Get all User")
    @GetMapping("/activities")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<List<AdminActivityResponseDto>>> getUsersActivities() {
        List<AdminActivityResponseDto> activities = adminService.getAdminActivities();
        return ResponseEntity.ok(ApiResponse.success("Activities retrieved successfully!", activities));
    }
    

    
    // Admin CRUD Operations
    @Operation(summary = "Get all Users", description = "Retrieve all admin users")
    @GetMapping("/admins_users")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<List<AdminResponseDto>>> getAllUsers() {
        List<AdminResponseDto> admins = adminService.getAllAdmins();
        return ResponseEntity.ok(ApiResponse.success("All users retrieved successfully!", admins));
    }

    @Operation(summary = "Get User by Id", description = "Retrieve admin user by ID")
    @GetMapping("/admins_users/{adminId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<AdminResponseDto>> getUsersById(@PathVariable Long adminId) {
        AdminResponseDto admin = adminService.getAdminById(adminId);
        return ResponseEntity.ok(ApiResponse.success("User retrieved successfully!", admin));
    }
    @Operation(summary = "Update User by Id", description = "Update admin user")
    @PutMapping("/admins_users/{adminId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<AdminResponseDto>> updateUsers(
            @PathVariable Long adminId,
            @Valid @RequestBody AdminCreateDto adminUpdateDto) {
        AdminResponseDto updatedAdmin = adminService.updateAdmin(adminId, adminUpdateDto);
        return ResponseEntity.ok(ApiResponse.success("User updated successfully!", updatedAdmin));
    }
    @Operation(summary = "Delete User by Id", description = "Delete admin user")
    @DeleteMapping("/admins_users/{adminId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteUsers(@PathVariable Long adminId) {
        adminService.deleteAdmin(adminId);
        return ResponseEntity.ok(ApiResponse.success("User deleted successfully!"));
    }

}
