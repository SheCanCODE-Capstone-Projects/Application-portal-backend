package com.igirerwanda.application_portal_backend.application.service;

import com.igirerwanda.application_portal_backend.application.dto.ApplicationCreateDto;
import com.igirerwanda.application_portal_backend.application.dto.ApplicationDto;
import com.igirerwanda.application_portal_backend.application.entity.Application;
import com.igirerwanda.application_portal_backend.cohort.entity.Cohort;
import com.igirerwanda.application_portal_backend.cohort.repository.CohortRepository;
import com.igirerwanda.application_portal_backend.common.enums.ApplicationStatus;
import com.igirerwanda.application_portal_backend.common.exception.DuplicateResourceException;
import com.igirerwanda.application_portal_backend.user.entity.User;
import com.igirerwanda.application_portal_backend.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApplicationServiceTest {

    @Mock
    private ApplicationRepository applicationRepository;
    
    @Mock
    private CohortRepository cohortRepository;
    
    @Mock
    private UserService userService;

    private ApplicationServiceImpl applicationService;
    private User testUser;
    private Cohort testCohort;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);

        testCohort = new Cohort();
        testCohort.setId(1L);
        testCohort.setName("Test Cohort");
    }

    @Test
    void createApplication_Success() {
        ApplicationCreateDto dto = new ApplicationCreateDto();
        dto.setCohortId(1L);

        when(applicationRepository.existsByUserIdAndCohortId(1L, 1L)).thenReturn(false);
        when(userService.findById(1L)).thenReturn(testUser);
        when(cohortRepository.findById(1L)).thenReturn(Optional.of(testCohort));

        Application savedApp = new Application();
        savedApp.setId(1L);
        savedApp.setUser(testUser);
        savedApp.setCohort(testCohort);
        savedApp.setStatus(ApplicationStatus.DRAFT);
        
        when(applicationRepository.save(any(Application.class))).thenReturn(savedApp);

        ApplicationDto result = applicationService.createApplication(1L, dto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(ApplicationStatus.DRAFT, result.getStatus());
    }

    @Test
    void createApplication_DuplicateThrowsException() {
        ApplicationCreateDto dto = new ApplicationCreateDto();
        dto.setCohortId(1L);

        when(applicationRepository.existsByUserIdAndCohortId(1L, 1L)).thenReturn(true);

        assertThrows(DuplicateResourceException.class, 
            () -> applicationService.createApplication(1L, dto));
    }
}