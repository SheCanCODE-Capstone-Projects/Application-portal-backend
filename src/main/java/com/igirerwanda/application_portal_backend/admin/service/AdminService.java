package com.igirerwanda.application_portal_backend.admin.service;

import com.igirerwanda.application_portal_backend.admin.dto.AdminCreateDto;
import com.igirerwanda.application_portal_backend.admin.dto.AdminResponseDto;
import com.igirerwanda.application_portal_backend.admin.entity.AdminActivity;

import java.util.List;
import java.util.UUID;

public interface AdminService {
    AdminResponseDto createAdmin(AdminCreateDto adminCreateDto);
    List<AdminActivity> getAdminActivities();

    // Admin CRUD
    List<AdminResponseDto> getAllAdmins();
    AdminResponseDto getAdminById(UUID adminId);
    AdminResponseDto updateAdmin(UUID adminId, AdminCreateDto adminUpdateDto);
    void deleteAdmin(UUID adminId);
}