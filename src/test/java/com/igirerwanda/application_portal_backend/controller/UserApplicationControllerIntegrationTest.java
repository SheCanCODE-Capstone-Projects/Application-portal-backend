package com.igirerwanda.application_portal_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.igirerwanda.application_portal_backend.application.dto.PersonalInfoDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class UserApplicationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    public void testStartApplication() throws Exception {
        mockMvc.perform(post("/api/v1/user/applications/start"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("DRAFT"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    public void testGetMyApplication() throws Exception {
        // First start an application
        mockMvc.perform(post("/api/v1/user/applications/start"))
                .andExpect(status().isOk());
        
        // Then retrieve it
        mockMvc.perform(get("/api/v1/user/applications/my-application"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("DRAFT"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    public void testSavePersonalInfo() throws Exception {
        // Start application first
        String response = mockMvc.perform(post("/api/v1/user/applications/start"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        
        // Extract application ID from response (simplified)
        Long appId = 1L; // In real test, parse from response
        
        PersonalInfoDto personalInfo = new PersonalInfoDto();
        personalInfo.setFullName("Test User");
        personalInfo.setEmail("test@example.com");
        personalInfo.setPhone("+250123456789");
        
        mockMvc.perform(put("/api/v1/user/applications/" + appId + "/personal-info")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(personalInfo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Personal information saved"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    public void testGetProgress() throws Exception {
        Long appId = 1L; // Assuming application exists
        
        mockMvc.perform(get("/api/v1/user/applications/" + appId + "/progress"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.percentage").exists());
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    public void testSubmitApplication() throws Exception {
        Long appId = 1L; // Assuming application exists with all required data
        
        mockMvc.perform(put("/api/v1/user/applications/" + appId + "/submit"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("SUBMITTED"));
    }
}