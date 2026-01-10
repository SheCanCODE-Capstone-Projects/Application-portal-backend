package com.igirerwanda.application_portal_backend.dto;

import com.igirerwanda.application_portal_backend.common.enums.AdminRole;
import jakarta.validation.constraints.NotNull;

public class AdminCreateDto {

    private String email;
    private String password;

    @NotNull(message = "Role is required")
    private AdminRole role;

    // getters and setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public AdminRole getRole() { return role; }
    public void setRole(AdminRole role) { this.role = role; }
}
