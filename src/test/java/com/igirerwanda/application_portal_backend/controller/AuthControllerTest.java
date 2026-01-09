package com.igirerwanda.application_portal_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.igirerwanda.application_portal_backend.auth.controller.AuthController;
import com.igirerwanda.application_portal_backend.auth.dto.LoginRequest;
import com.igirerwanda.application_portal_backend.auth.dto.LoginResponse;
import com.igirerwanda.application_portal_backend.auth.dto.RegisterRequest;
import com.igirerwanda.application_portal_backend.auth.service.AuthService;
import com.igirerwanda.application_portal_backend.config.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    // REQUIRED: Mocks the dependency for JwtAuthenticationFilter
    @MockBean
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void register_Success() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@example.com");
        request.setUsername("testuser");
        request.setPassword("password123");

        when(authService.register(any(RegisterRequest.class)))
                .thenReturn(Map.of("message", "Verification email sent"));

        mockMvc.perform(post("/api/v1/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Verification email sent"));
    }

    @Test
    void login_Success() throws Exception {
        LoginRequest request = new LoginRequest("test@example.com", "password123");
        LoginResponse response = new LoginResponse("mock-jwt-token");

        when(authService.login(any(LoginRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mock-jwt-token"));
    }
}