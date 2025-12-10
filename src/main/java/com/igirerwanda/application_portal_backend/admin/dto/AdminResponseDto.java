package com.igirerwanda.application_portal_backend.admin.dto;

public class AdminResponseDto {
    private Long id;
    private String email;
    private String role;
    
    public AdminResponseDto() {}
    
    public AdminResponseDto(Long id, String email, String role) {
        this.id = id;
        this.email = email;
        this.role = role;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}