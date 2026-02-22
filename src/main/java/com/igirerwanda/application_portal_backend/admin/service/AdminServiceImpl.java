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
import com.igirerwanda.application_portal_backend.notification.service.WebSocketEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor // Automatically creates a constructor for all 'final' fields (fixes Field Injection warnings)
public class AdminServiceImpl implements AdminService {

    private final AdminUserRepository adminUserRepository;
    private final AdminActivityRepository adminActivityRepository;
    private final RegisterRepository registerRepository;
    private final WebSocketEventService webSocketEventService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public AdminResponseDto createAdmin(AdminCreateDto adminCreateDto) {
        if (adminUserRepository.existsByEmail(adminCreateDto.getEmail()) ||
                registerRepository.existsByEmail(adminCreateDto.getEmail())) {
            throw new DuplicateResourceException("Admin email already exists: " + adminCreateDto.getEmail());
        }

        Register register = new Register();
        register.setEmail(adminCreateDto.getEmail());
        register.setUsername(adminCreateDto.getName().toLowerCase().replace(" ", "."));
        register.setPassword(passwordEncoder.encode(adminCreateDto.getPassword()));
        register.setRole(UserRole.ADMIN);
        register.setProvider(AuthProvider.LOCAL);
        register.setVerified(true);

        registerRepository.save(register);

        AdminUser adminUser = new AdminUser();
        adminUser.setName(adminCreateDto.getName());
        adminUser.setEmail(adminCreateDto.getEmail());
        adminUser.setRole(adminCreateDto.getRole());
        adminUser.setPassword(passwordEncoder.encode(adminCreateDto.getPassword()));

        AdminUser savedAdmin = adminUserRepository.save(adminUser);

        logAdminActivityWithAdmin("CREATED_ADMIN", savedAdmin, "Created new admin: " + savedAdmin.getEmail());

        webSocketEventService.broadcastToAdmins("ADMIN_CREATED", Map.of(
                "id", savedAdmin.getId(),
                "name", savedAdmin.getName(),
                "email", savedAdmin.getEmail(),
                "role", savedAdmin.getRole().toString()
        ));

        return mapToAdminResponseDto(savedAdmin);
    }

    @Override
    public List<AdminActivity> getAdminActivities() {
        return adminActivityRepository.findAllByOrderByCreatedAtDesc();
    }

    @Override
    public List<AdminResponseDto> getAllAdmins() {
        return adminUserRepository.findAll().stream()
                .map(this::mapToAdminResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public AdminResponseDto getAdminById(UUID adminId) {
        AdminUser admin = adminUserRepository.findById(adminId)
                .orElseThrow(() -> new NotFoundException("Admin not found with id: " + adminId));
        return mapToAdminResponseDto(admin);
    }

    @Override
    public AdminResponseDto updateAdmin(UUID adminId, AdminCreateDto adminUpdateDto) {
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
    public void deleteAdmin(UUID adminId) {
        AdminUser admin = adminUserRepository.findById(adminId)
                .orElseThrow(() -> new NotFoundException("Admin not found with id: " + adminId));

        adminActivityRepository.findByAdmin_EmailOrderByCreatedAtDesc(admin.getEmail())
                .forEach(adminActivityRepository::delete);

        adminUserRepository.delete(admin);

        AdminActivity activity = new AdminActivity();
        activity.setAction("DELETED_ADMIN");
        activity.setEmail(admin.getEmail());
        adminActivityRepository.save(activity);
    }

    @Override
    public Map<String, Object> getSystemHealthData() {
        List<Map<String, Object>> services = new ArrayList<>();
        String[] serviceNames = {
                "Core Application API",
                "Database Cluster",
                "Authentication Server",
                "File Storage System"
        };

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
        LocalDate today = LocalDate.now();

        for (String name : serviceNames) {
            List<Map<String, String>> dailyData = new ArrayList<>();
            int operationalDays = 0;

            for (int i = 89; i >= 0; i--) {
                LocalDate date = today.minusDays(i);

                double rand = Math.random();
                String status = "Operational";
                if (rand > 0.98) status = "Downtime";
                else if (rand > 0.96) status = "Maintenance";

                if ("Operational".equals(status)) operationalDays++;

                dailyData.add(Map.of(
                        "date", date.format(dateFormatter),
                        "tooltip", status
                ));
            }

            double uptime = Math.round(((double) operationalDays / 90.0) * 1000.0) / 10.0;

            services.add(Map.of(
                    "name", name,
                    "uptime", String.valueOf(uptime),
                    "data", dailyData
            ));
        }

        return Map.of(
                "lastUpdated", LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm:ss")),
                "services", services
        );
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