package com.igirerwanda.application_portal_backend.auth.dto;

import com.igirerwanda.application_portal_backend.common.enums.UserRole;

public class LoginResponse {
    
    private String token;
    private UserRole role;
    private Long userId;
    
    public LoginResponse() {}
    
    public LoginResponse(String token, UserRole role, Long userId) {
        this.token = token;
        this.role = role;
        this.userId = userId;
    }
    
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public UserRole getRole() {
        return role;
    }
    
    public void setRole(UserRole role) {
        this.role = role;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
