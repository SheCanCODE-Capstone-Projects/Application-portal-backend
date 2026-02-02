package com.igirerwanda.application_portal_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.igirerwanda.application_portal_backend.application.controller.UserApplicationController;
import com.igirerwanda.application_portal_backend.application.dto.ApplicationDto;
import com.igirerwanda.application_portal_backend.application.dto.PersonalInfoDto;
import com.igirerwanda.application_portal_backend.application.service.UserApplicationService;
import com.igirerwanda.application_portal_backend.common.enums.ApplicationStatus;
import com.igirerwanda.application_portal_backend.config.JwtService;
import com.igirerwanda.application_portal_backend.security.UserPrincipal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserApplicationController.class)
public class ApplicationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserApplicationService userApplicationService;

    @MockBean
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    void startApplication_Success() throws Exception {
        // Given
        ApplicationDto response = new ApplicationDto();
        response.setId(100L);
        response.setStatus(ApplicationStatus.DRAFT);

        // Note: For actual UserPrincipal mapping, more complex setup might be needed
        // but for @WithMockUser, the security context is populated with a basic user
        when(userApplicationService.startApplicationForUser(any())).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/api/v1/applications/start")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(100L))
                .andExpect(jsonPath("$.status").value("DRAFT"));
    }

    @Test
    @WithMockUser
    void savePersonalInfo_Success() throws Exception {
        // Given
        Long appId = 100L;
        PersonalInfoDto infoDto = new PersonalInfoDto();
        infoDto.setFullName("Test User");

        ApplicationDto response = new ApplicationDto();
        response.setId(appUUID);

        when(userApplicationService.savePersonalInfo(eq(appId), any(), any(PersonalInfoDto.class)))
                .thenReturn(response);

        // When & Then
        mockMvc.perform(post("/api/v1/applications/{applicationId}/personal-info", appId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(infoDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(appId));
    }
}