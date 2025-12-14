package com.igirerwanda.application_portal_backend.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.igirerwanda.application_portal_backend.auth.dto.ResendVerificationRequest;
import com.igirerwanda.application_portal_backend.auth.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void resendVerificationEmail_ValidRequest_ShouldReturn200() throws Exception {
        // Given
        ResendVerificationRequest request = new ResendVerificationRequest();
        request.setEmail("test@example.com");

        // When & Then
        mockMvc.perform(post("/api/v1/verify/resend")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("A new verification email has been sent."));

        verify(authService).resendVerificationEmail("test@example.com");
    }

    @Test
    void resendVerificationEmail_AlreadyVerified_ShouldReturn409() throws Exception {
        // Given
        ResendVerificationRequest request = new ResendVerificationRequest();
        request.setEmail("verified@example.com");
        
        doThrow(new RuntimeException("User already verified"))
                .when(authService).resendVerificationEmail("verified@example.com");

        // When & Then
        mockMvc.perform(post("/api/v1/verify/resend")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("User already verified"));
    }

    @Test
    void resendVerificationEmail_InvalidEmail_ShouldReturn400() throws Exception {
        // Given
        ResendVerificationRequest request = new ResendVerificationRequest();
        request.setEmail("invalid-email");

        // When & Then
        mockMvc.perform(post("/api/v1/verify/resend")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(authService, never()).resendVerificationEmail(anyString());
    }

    @Test
    void resendVerificationEmail_EmptyEmail_ShouldReturn400() throws Exception {
        // Given
        ResendVerificationRequest request = new ResendVerificationRequest();
        request.setEmail("");

        // When & Then
        mockMvc.perform(post("/api/v1/verify/resend")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(authService, never()).resendVerificationEmail(anyString());
    }
}