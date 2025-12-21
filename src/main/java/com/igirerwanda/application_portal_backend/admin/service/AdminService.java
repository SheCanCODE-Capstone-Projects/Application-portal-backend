package com.igirerwanda.application_portal_backend.admin.service;

import com.igirerwanda.application_portal_backend.admin.dto.AdminCreateDto;
import com.igirerwanda.application_portal_backend.admin.dto.AdminResponseDto;
import com.igirerwanda.application_portal_backend.admin.dto.AdminActivityResponseDto;

import java.util.List;

public interface AdminService {
    AdminResponseDto createAdmin(AdminCreateDto adminCreateDto);
    List<AdminActivityResponseDto> getAdminActivities();
    

    
    // Admin CRUD
    List<AdminResponseDto> getAllAdmins();
    AdminResponseDto getAdminById(Long adminId);
    AdminResponseDto updateAdmin(Long adminId, AdminCreateDto adminUpdateDto);
    void deleteAdmin(Long adminId);
}
