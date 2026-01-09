package com.igirerwanda.application_portal_backend.controller;

import com.igirerwanda.application_portal_backend.application.dto.ApplicationDto;
import com.igirerwanda.application_portal_backend.application.dto.PersonalInfoDto;
import com.igirerwanda.application_portal_backend.application.entity.Application;
import com.igirerwanda.application_portal_backend.application.entity.PersonalInformation;
import com.igirerwanda.application_portal_backend.application.repository.*;
import com.igirerwanda.application_portal_backend.application.service.ApplicationValidationService;
import com.igirerwanda.application_portal_backend.application.service.UserApplicationServiceImpl;
import com.igirerwanda.application_portal_backend.cohort.entity.Cohort;
import com.igirerwanda.application_portal_backend.common.enums.ApplicationStatus;
import com.igirerwanda.application_portal_backend.auth.entity.Register;
import com.igirerwanda.application_portal_backend.user.entity.User;
import com.igirerwanda.application_portal_backend.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserApplicationServiceTest {

    @InjectMocks
    private UserApplicationServiceImpl userApplicationService;

    @Mock private ApplicationRepository applicationRepository;
    @Mock private UserService userService;
    @Mock private PersonalInfoRepository personalInfoRepository;
    @Mock private ApplicationValidationService validationService;

    // --- Mocks required for internal mapping (mapToDto) ---
    @Mock private DocumentRepository documentRepository;
    @Mock private EducationalRepository educationalRepository;
    @Mock private DisabilityRepository disabilityRepository;
    @Mock private EmergencyContactRepository emergencyContactRepository;
    @Mock private VulnerabilityInformationRepository vulnerabilityInformationRepository;
    @Mock private MotivationAnswerRepository motivationAnswerRepository;

    private User testUser;
    private Register testRegister;
    private Cohort testCohort;
    private Application testApplication;

    @BeforeEach
    void setUp() {
        testRegister = new Register();
        testRegister.setId(99L);
        testRegister.setEmail("test@test.com");

        testCohort = new Cohort();
        testCohort.setId(1L);
        testCohort.setName("Cohort 1");

        testUser = new User();
        testUser.setId(10L);
        testUser.setRegister(testRegister);
        testUser.setCohort(testCohort);

        testApplication = new Application();
        testApplication.setId(50L);
        testApplication.setUser(testUser);
        testApplication.setCohort(testCohort);
        testApplication.setStatus(ApplicationStatus.DRAFT);
    }

    @Test
    void startApplicationForUser_Success() {
        // Given
        when(userService.findByRegisterId(99L)).thenReturn(testUser);
        when(applicationRepository.findByUserIdAndCohortId(10L, 1L)).thenReturn(Optional.empty());
        when(applicationRepository.save(any(Application.class))).thenReturn(testApplication);

        // When
        ApplicationDto result = userApplicationService.startApplicationForUser(99L);

        // Then
        assertNotNull(result);
        assertEquals(ApplicationStatus.DRAFT, result.getStatus());
        verify(applicationRepository).save(any(Application.class));
    }

    @Test
    void savePersonalInfo_Success() {
        // Given
        PersonalInfoDto infoDto = new PersonalInfoDto();
        infoDto.setFullName("John Doe");

        when(applicationRepository.findById(50L)).thenReturn(Optional.of(testApplication));
        when(personalInfoRepository.save(any(PersonalInformation.class))).thenReturn(new PersonalInformation());

        // Mock document repository to prevent NPE during DTO mapping
        when(documentRepository.findByPersonalInformation(any())).thenReturn(Collections.emptyList());

        // When
        ApplicationDto result = userApplicationService.savePersonalInfo(50L, 99L, infoDto);

        // Then
        assertNotNull(result);
        verify(personalInfoRepository).save(any(PersonalInformation.class));
    }

    @Test
    void savePersonalInfo_NotOwner_ThrowsException() {
        // Given
        Register otherRegister = new Register();
        otherRegister.setId(88L); // Different ID
        testUser.setRegister(otherRegister);

        when(applicationRepository.findById(50L)).thenReturn(Optional.of(testApplication));

        // When & Then
        PersonalInfoDto dto = new PersonalInfoDto();
        assertThrows(AccessDeniedException.class, () ->
                userApplicationService.savePersonalInfo(50L, 99L, dto)
        );
    }
}