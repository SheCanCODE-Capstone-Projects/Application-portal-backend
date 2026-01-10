package com.igirerwanda.application_portal_backend.dto;

import com.igirerwanda.application_portal_backend.common.enums.AdminRole;

public class AdminResponseDto {
    private Long id;
    private String email;
    private AdminRole role;

    // getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public AdminRole getRole() { return role; }
    public void setRole(AdminRole role) { this.role = role; }
}
