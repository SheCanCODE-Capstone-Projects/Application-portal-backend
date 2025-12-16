package com.igirerwanda.application_portal_backend.admin.controller;

import com.igirerwanda.application_portal_backend.admin.dto.*;
import com.igirerwanda.application_portal_backend.admin.entity.AdminActivity;
import com.igirerwanda.application_portal_backend.admin.service.AdminService;
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
    
    @PostMapping("/admins")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<AdminResponseDto> createAdmin(@Valid @RequestBody AdminCreateDto adminCreateDto) {
        AdminResponseDto response = adminService.createAdmin(adminCreateDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    @GetMapping("/activities")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<AdminActivity>> getAdminActivities() {
        List<AdminActivity> activities = adminService.getAdminActivities();
        return ResponseEntity.ok(activities);
    }
    

    
    // Admin CRUD Operations
    @GetMapping("/admins")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<AdminResponseDto>> getAllAdmins() {
        List<AdminResponseDto> admins = adminService.getAllAdmins();
        return ResponseEntity.ok(admins);
    }
    
    @GetMapping("/admins/{adminId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<AdminResponseDto> getAdminById(@PathVariable Long adminId) {
        AdminResponseDto admin = adminService.getAdminById(adminId);
        return ResponseEntity.ok(admin);
    }
    
    @PutMapping("/admins/{adminId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<AdminResponseDto> updateAdmin(
            @PathVariable Long adminId,
            @Valid @RequestBody AdminCreateDto adminUpdateDto) {
        AdminResponseDto updatedAdmin = adminService.updateAdmin(adminId, adminUpdateDto);
        return ResponseEntity.ok(updatedAdmin);
    }
    
    @DeleteMapping("/admins/{adminId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteAdmin(@PathVariable Long adminId) {
        adminService.deleteAdmin(adminId);
        return ResponseEntity.noContent().build();
    }
    

    

}
