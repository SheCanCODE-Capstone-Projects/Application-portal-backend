package com.igirerwanda.application_portal_backend.application.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.igirerwanda.application_portal_backend.application.dto.ApplicationCreateDto;
import com.igirerwanda.application_portal_backend.application.dto.ApplicationDto;
import com.igirerwanda.application_portal_backend.common.enums.ApplicationStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ApplicationController.class)
class ApplicationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ApplicationService applicationService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createApplication_Success() throws Exception {
        ApplicationCreateDto dto = new ApplicationCreateDto();
        dto.setCohortId(1L);

        ApplicationDto responseDto = new ApplicationDto();
        responseDto.setId(1L);
        responseDto.setCohortId(1L);
        responseDto.setStatus(ApplicationStatus.DRAFT);

        when(applicationService.createApplication(any(Long.class), any(ApplicationCreateDto.class)))
                .thenReturn(responseDto);

        mockMvc.perform(post("/api/v1/applications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.cohortId").value(1L))
                .andExpect(jsonPath("$.status").value("DRAFT"));
    }

    @Test
    void getApplication_Success() throws Exception {
        ApplicationDto responseDto = new ApplicationDto();
        responseDto.setId(1L);
        responseDto.setStatus(ApplicationStatus.SUBMITTED);

        when(applicationService.getApplication(1L)).thenReturn(responseDto);

        mockMvc.perform(get("/api/v1/applications/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("SUBMITTED"));
    }

    @Test
    void createApplication_InvalidData_BadRequest() throws Exception {
        ApplicationCreateDto dto = new ApplicationCreateDto();
        // Missing cohortId

        mockMvc.perform(post("/api/v1/applications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }
}