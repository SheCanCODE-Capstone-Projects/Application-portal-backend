package com.igirerwanda.application_portal_backend.user.dto;

import com.igirerwanda.application_portal_backend.common.enums.UserStatus;
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
}