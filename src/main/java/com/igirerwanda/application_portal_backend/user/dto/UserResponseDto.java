package com.igirerwanda.application_portal_backend.user.dto;

import com.igirerwanda.application_portal_backend.common.enums.UserStatus;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserResponseDto {
    private Long id;
    private String username;
    private String email;
    private UserStatus status;
    private Long cohortId;
    private String cohortName;
    private LocalDateTime createdAt;
}