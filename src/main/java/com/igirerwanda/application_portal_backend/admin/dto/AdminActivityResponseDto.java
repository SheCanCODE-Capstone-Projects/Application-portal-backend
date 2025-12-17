package com.igirerwanda.application_portal_backend.admin.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class AdminActivityResponseDto {
    private Long id;
    private String action;
    private LocalDateTime createdAt;
    private String email;
    
    // Admin details
    private Long adminId;
    private String adminName;
    private String adminEmail;
    
    // Application details (if applicable)
    private Long applicationId;
    private String applicantName;
}