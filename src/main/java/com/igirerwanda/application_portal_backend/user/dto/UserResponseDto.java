package com.igirerwanda.application_portal_backend.user.dto;

import com.igirerwanda.application_portal_backend.common.enums.UserStatus;
import com.igirerwanda.application_portal_backend.common.enums.AuthProvider;
import com.igirerwanda.application_portal_backend.common.enums.UserRole;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class UserResponseDto {
    private UUID id;
    private String username;
    private String email;
    private UserStatus status;
    private UUID cohortId;
    private String cohortName;
    private LocalDateTime createdAt;

    // --- NEW FIELDS ---
    private AuthProvider provider;
    private UserRole role;
}