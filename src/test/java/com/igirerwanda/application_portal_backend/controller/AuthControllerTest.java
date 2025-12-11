package com.igirerwanda.application_portal_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.igirerwanda.application_portal_backend.auth.controller.AuthController;
import com.igirerwanda.application_portal_backend.auth.dto.RegisterRequest;
import com.igirerwanda.application_portal_backend.auth.service.AuthService;
import com.igirerwanda.application_portal_backend.common.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
public class AuthControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private AuthService authService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private RegisterRequest registerRequest;
    
    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setEmail("user@example.com");
        registerRequest.setFirstName("John");
        registerRequest.setLastName("Doe");
        registerRequest.setPassword("password123");
    }
    
    @Test
    void register_WithAdminRole_RejectRequest() throws Exception {
        // Given
        registerRequest.setRole(UserRole.ADMIN);
        
        // When & Then
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Admin roles cannot be created via public registration"));
    }
    
    @Test
    void register_WithSuperAdminRole_RejectRequest() throws Exception {
        // Given
        registerRequest.setRole(UserRole.SUPER_ADMIN);
        
        // When & Then
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Admin roles cannot be created via public registration"));
    }
    
    @Test
    void register_WithUserRole_Success() throws Exception {
        // Given
        registerRequest.setRole(UserRole.USER);
        when(authService.register(any(RegisterRequest.class)))
            .thenReturn(ResponseEntity.ok("User registered successfully"));
        
        // When & Then
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk());
    }
    
    @Test
    void register_WithNullRole_Success() throws Exception {
        // Given - role is null, should default to USER
        when(authService.register(any(RegisterRequest.class)))
            .thenReturn(ResponseEntity.ok("User registered successfully"));
        
        // When & Then
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk());
    }
}