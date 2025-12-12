package com.igirerwanda.application_portal_backend.admin.service;

import com.igirerwanda.application_portal_backend.admin.dto.AdminCreateDto;
import com.igirerwanda.application_portal_backend.admin.dto.AdminResponseDto;
import com.igirerwanda.application_portal_backend.auth.repository.UserRepository;
import com.igirerwanda.application_portal_backend.common.enums.UserRole;
import com.igirerwanda.application_portal_backend.common.exception.DuplicateResourceException;
import com.igirerwanda.application_portal_backend.user.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminServiceImpl implements AdminService {
    private static final Logger logger = LoggerFactory.getLogger(AdminServiceImpl.class);
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    @Transactional
    public AdminResponseDto createAdmin(AdminCreateDto adminCreateDto) {
        // Check if user already exists
        if (userRepository.existsByEmail(adminCreateDto.getEmail())) {
            throw new DuplicateResourceException("User with email already exists");
        }
        
        // Create new admin user
        User admin = new User();
        admin.setEmail(adminCreateDto.getEmail());
        admin.setFirstName(adminCreateDto.getFirstName());
        admin.setLastName(adminCreateDto.getLastName());
        admin.setPhone(adminCreateDto.getPhone());
        admin.setPassword(passwordEncoder.encode(adminCreateDto.getSetPassword()));
        admin.setRole(UserRole.ADMIN);
        admin.setRequirePasswordReset(true); // Force password reset on first login
        admin.setMfaEnabled(true); // Require MFA for admins
        
        User savedAdmin = userRepository.save(admin);
        
        // Log admin creation for audit
        logger.info("Admin user created: email={}, id={}", savedAdmin.getEmail(), savedAdmin.getId());
        
        return new AdminResponseDto(savedAdmin.getId(), savedAdmin.getEmail(), savedAdmin.getRole().name());
    }
}
