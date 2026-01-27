package com.igirerwanda.application_portal_backend.admin.dto;

import com.igirerwanda.application_portal_backend.common.enums.AdminRole;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class AdminResponseDto {
    private UUID id;
    private String name;
    private String email;
    private AdminRole role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}