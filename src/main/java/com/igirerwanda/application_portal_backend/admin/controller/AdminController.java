package com.igirerwanda.application_portal_backend.admin.controller;

import com.igirerwanda.application_portal_backend.admin.dto.AdminCreateDto;
import com.igirerwanda.application_portal_backend.admin.dto.AdminResponseDto;
import com.igirerwanda.application_portal_backend.admin.entity.AdminActivity;
import com.igirerwanda.application_portal_backend.common.util.ApiResponse;
import com.igirerwanda.application_portal_backend.admin.service.AdminService;
import com.igirerwanda.application_portal_backend.auth.entity.Register;
import com.igirerwanda.application_portal_backend.auth.repository.RegisterRepository;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin-controller")
public class AdminController {
    
    @Autowired
    private AdminService adminService;
    
    @Autowired
    private RegisterRepository registerRepository;

    @Operation(summary = "Admin create User", description = "Admin create  User")
    @PostMapping("/admins_users")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<AdminResponseDto>> createUsers(@Valid @RequestBody AdminCreateDto adminCreateDto) {
        AdminResponseDto response = adminService.createAdmin(adminCreateDto);
        return new ResponseEntity<>(ApiResponse.success("User created successfully!", response), HttpStatus.CREATED);
    }

    @Operation(summary = "Get all activities", description = "Get all activities")
    @GetMapping("/activities")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<List<AdminActivity>>> getUsersActivities() {
        List<AdminActivity> activities = adminService.getAdminActivities();
        return ResponseEntity.ok(new ApiResponse<>(true, "Activities retrieved successfully!", activities));
    }
    

    
    @Operation(summary = "Get all Users", description = "Retrieve all users including applicants")
    @GetMapping("/getAllUsers")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<List<Register>>> getAllUsers() {
        List<Register> users = registerRepository.findAll();
        return ResponseEntity.ok(new ApiResponse<>(true, "All users retrieved successfully!", users));
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
