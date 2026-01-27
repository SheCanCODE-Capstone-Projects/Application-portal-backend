package com.igirerwanda.application_portal_backend.admin.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class AdminActivityResponseDto {
    private UUID id;
    private String action;
    private LocalDateTime createdAt;
    private String adminName;
    private String adminEmail;
}