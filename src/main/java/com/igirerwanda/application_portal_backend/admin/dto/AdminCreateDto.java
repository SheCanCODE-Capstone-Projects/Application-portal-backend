package com.igirerwanda.application_portal_backend.admin.dto;

import com.igirerwanda.application_portal_backend.common.enums.AdminRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AdminCreateDto {
    @NotBlank(message = "Name is required")
    private String name;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    
    @NotNull(message = "Role is required")
    private AdminRole role;
    
    @NotBlank(message = "Password is required")
    private String password;
}