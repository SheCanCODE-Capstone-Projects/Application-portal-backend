package com.igirerwanda.application_portal_backend.admin.service;

import com.igirerwanda.application_portal_backend.admin.dto.*;
import com.igirerwanda.application_portal_backend.admin.entity.AdminActivity;
import com.igirerwanda.application_portal_backend.admin.entity.AdminUser;
import com.igirerwanda.application_portal_backend.admin.repository.AdminActivityRepository;
import com.igirerwanda.application_portal_backend.admin.repository.AdminUserRepository;
import com.igirerwanda.application_portal_backend.auth.entity.Register;
import com.igirerwanda.application_portal_backend.auth.repository.RegisterRepository;
import com.igirerwanda.application_portal_backend.common.enums.AuthProvider;
import com.igirerwanda.application_portal_backend.common.enums.UserRole;
import com.igirerwanda.application_portal_backend.common.exception.DuplicateResourceException;
import com.igirerwanda.application_portal_backend.common.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import java.util.List;


@Service
@Transactional
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminUserRepository adminUserRepository;
    
    @Autowired
    private AdminActivityRepository adminActivityRepository;
    
    @Autowired
    private RegisterRepository registerRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public AdminResponseDto createAdmin(AdminCreateDto adminCreateDto) {
        // Check if email already exists in both tables
        if (adminUserRepository.existsByEmail(adminCreateDto.getEmail()) || 
            registerRepository.existsByEmail(adminCreateDto.getEmail())) {
            throw new DuplicateResourceException("Admin email already exists: " + adminCreateDto.getEmail());
        }
        
        // Create Register entity for authentication
        Register register = new Register();
        register.setEmail(adminCreateDto.getEmail());
        register.setUsername(adminCreateDto.getName().toLowerCase().replace(" ", "."));
        register.setPassword(passwordEncoder.encode(adminCreateDto.getPassword()));
        register.setRole(UserRole.ADMIN);
        register.setProvider(AuthProvider.LOCAL);
        register.setVerified(true);
        
        registerRepository.save(register);
        
        // Create AdminUser entity
        AdminUser adminUser = new AdminUser();
        adminUser.setName(adminCreateDto.getName());
        adminUser.setEmail(adminCreateDto.getEmail());
        adminUser.setRole(adminCreateDto.getRole());
        adminUser.setPassword(passwordEncoder.encode(adminCreateDto.getPassword()));
        
        AdminUser savedAdmin = adminUserRepository.save(adminUser);
        
        // Log admin activity with the saved admin
        logAdminActivityWithAdmin("CREATED_ADMIN", savedAdmin, "Created new admin: " + savedAdmin.getEmail());
        
        return mapToAdminResponseDto(savedAdmin);
    }
    
    @Override
    public List<AdminActivityResponseDto> getAdminActivities() {
        return adminActivityRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::mapToAdminActivityResponseDto)
                .collect(Collectors.toList());
    }
    

    
    @Override
    public List<AdminResponseDto> getAllAdmins() {
        return adminUserRepository.findAll().stream()
                .map(this::mapToAdminResponseDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public AdminResponseDto getAdminById(Long adminId) {
        AdminUser admin = adminUserRepository.findById(adminId)
                .orElseThrow(() -> new NotFoundException("Admin not found with id: " + adminId));
        return mapToAdminResponseDto(admin);
    }
    
    @Override
    public AdminResponseDto updateAdmin(Long adminId, AdminCreateDto adminUpdateDto) {
        AdminUser admin = adminUserRepository.findById(adminId)
                .orElseThrow(() -> new NotFoundException("Admin not found with id: " + adminId));
        
        admin.setName(adminUpdateDto.getName());
        admin.setEmail(adminUpdateDto.getEmail());
        admin.setRole(adminUpdateDto.getRole());
        if (adminUpdateDto.getPassword() != null && !adminUpdateDto.getPassword().isEmpty()) {
            admin.setPassword(passwordEncoder.encode(adminUpdateDto.getPassword()));
        }
        
        AdminUser savedAdmin = adminUserRepository.save(admin);
        logAdminActivityWithAdmin("UPDATED_ADMIN", savedAdmin, "Updated admin: " + savedAdmin.getEmail());
        
        return mapToAdminResponseDto(savedAdmin);
    }
    
    @Override
    public void deleteAdmin(Long adminId) {
        AdminUser admin = adminUserRepository.findById(adminId)
                .orElseThrow(() -> new NotFoundException("Admin not found with id: " + adminId));
        
        // Delete all activities related to this admin first
        adminActivityRepository.findByAdmin_EmailOrderByCreatedAtDesc(admin.getEmail())
                .forEach(adminActivityRepository::delete);
        
        // Delete the admin user
        adminUserRepository.delete(admin);
        
        // Log deletion activity without admin reference
        AdminActivity activity = new AdminActivity();
        activity.setAction("DELETED_ADMIN");
        activity.setEmail(admin.getEmail());
        adminActivityRepository.save(activity);
    }
    



    
    private AdminActivityResponseDto mapToAdminActivityResponseDto(AdminActivity activity) {
        AdminActivityResponseDto dto = new AdminActivityResponseDto();
        dto.setId(activity.getId());
        dto.setAction(activity.getAction());
        dto.setCreatedAt(activity.getCreatedAt());
        dto.setAdminName(activity.getAdminName());
        dto.setAdminEmail(activity.getAdminEmail());
        return dto;
    }
    
    private AdminResponseDto mapToAdminResponseDto(AdminUser adminUser) {
        AdminResponseDto dto = new AdminResponseDto();
        dto.setId(adminUser.getId());
        dto.setName(adminUser.getName());
        dto.setEmail(adminUser.getEmail());
        dto.setRole(adminUser.getRole());
        dto.setCreatedAt(adminUser.getCreatedAt());
        dto.setUpdatedAt(adminUser.getUpdatedAt());
        return dto;
    }
    
    private void logAdminActivity(String action, String details) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            AdminActivity activity = new AdminActivity();
            activity.setAction(action);
            activity.setEmail(auth.getName());
            
            // Try to find the AdminUser by email and set the relationship
            adminUserRepository.findByEmail(auth.getName())
                .ifPresent(activity::setAdmin);
            
            adminActivityRepository.save(activity);
        } else {
            AdminActivity activity = new AdminActivity();
            activity.setAction(action);
            activity.setEmail("system");
            adminActivityRepository.save(activity);
        }
    }
    
    private void logAdminActivityWithAdmin(String action, AdminUser targetAdmin, String details) {
        AdminActivity activity = new AdminActivity();
        activity.setAction(action);
        activity.setEmail(targetAdmin.getEmail());
        activity.setAdmin(targetAdmin);
        
        adminActivityRepository.save(activity);
    }
    

}
