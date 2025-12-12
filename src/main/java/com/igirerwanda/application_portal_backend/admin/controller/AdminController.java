package com.igirerwanda.application_portal_backend.admin.controller;

import com.igirerwanda.application_portal_backend.admin.dto.AdminCreateDto;
import com.igirerwanda.application_portal_backend.admin.dto.AdminResponseDto;
import com.igirerwanda.application_portal_backend.admin.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {
    
    @Autowired
    private AdminService adminService;
    
    @PostMapping("/users")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasAuthority('ADMIN_MANAGE')")
    public ResponseEntity<AdminResponseDto> createAdmin(@Valid @RequestBody AdminCreateDto adminCreateDto) {
        AdminResponseDto response = adminService.createAdmin(adminCreateDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
