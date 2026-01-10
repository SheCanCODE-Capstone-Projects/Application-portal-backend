package com.igirerwanda.application_portal_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.igirerwanda.application_portal_backend.cohort.controller.CohortController;
import com.igirerwanda.application_portal_backend.cohort.dto.CohortCreateDto;
import com.igirerwanda.application_portal_backend.cohort.dto.CohortDto;
import com.igirerwanda.application_portal_backend.cohort.service.CohortService;
import com.igirerwanda.application_portal_backend.config.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CohortController.class)
class CohortControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CohortService cohortService;

    // REQUIRED: Mocks the dependency for JwtAuthenticationFilter
    @MockBean
    private JwtService jwtService;

    // REQUIRED: Mocks websocket template if controller uses it
    @MockBean
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "ADMIN")
    void createCohort_Success() throws Exception {
        CohortCreateDto createDto = new CohortCreateDto();
        createDto.setName("Cohort 1");
        createDto.setStartDate(LocalDate.now());
        createDto.setEndDate(LocalDate.now().plusMonths(3));

        CohortDto responseDto = new CohortDto();
        responseDto.setId(1L);
        responseDto.setName("Cohort 1");

        when(cohortService.createCohort(any(CohortCreateDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/v1/admin/cohorts")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Cohort 1"));
    }

    @Test
    void getCohortsForFrontend_Success() throws Exception {
        CohortDto dto = new CohortDto();
        dto.setId(1L);
        dto.setName("Open Cohort");

        when(cohortService.getCohortsForFrontend()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/v1/cohorts/frontend"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Open Cohort"));
    }
}