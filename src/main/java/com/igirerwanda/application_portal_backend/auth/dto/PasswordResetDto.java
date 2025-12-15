package com.igirerwanda.application_portal_backend.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordResetDto {
    private String token;
    private String newPassword;
}
