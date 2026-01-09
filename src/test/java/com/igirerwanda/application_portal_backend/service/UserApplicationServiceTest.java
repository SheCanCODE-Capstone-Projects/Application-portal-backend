package com.igirerwanda.application_portal_backend.service;

import com.igirerwanda.application_portal_backend.application.dto.*;
import com.igirerwanda.application_portal_backend.application.entity.*;
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

    @Mock
    private ApplicationRepository applicationRepository;
    @Mock
    private UserService userService;
    @Mock
    private PersonalInfoRepository personalInfoRepository;
    @Mock
    private ApplicationValidationService validationService;
    @Mock
    private DocumentRepository documentRepository; // Required to prevent NullPointerException in mapToDto

    @InjectMocks
    private UserApplicationServiceImpl userApplicationService;

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
    void startApplicationForUser_NoCohortSelected_ThrowsException() {
        // Given
        testUser.setCohort(null);
        when(userService.findByRegisterId(99L)).thenReturn(testUser);

        // When & Then
        assertThrows(IllegalStateException.class, () ->
                userApplicationService.startApplicationForUser(99L)
        );
    }

    @Test
    void savePersonalInfo_Success() {
        // Given
        PersonalInfoDto infoDto = new PersonalInfoDto();
        infoDto.setFullName("John Doe");
        infoDto.setEmail("john@test.com");

        when(applicationRepository.findById(50L)).thenReturn(Optional.of(testApplication));
        when(personalInfoRepository.save(any(PersonalInformation.class))).thenReturn(new PersonalInformation());

        // Mocking document repository calls used in mapToDto
        when(documentRepository.findByPersonalInformation(any())).thenReturn(Collections.emptyList());

        // When
        ApplicationDto result = userApplicationService.savePersonalInfo(50L, 99L, infoDto);

        // Then
        assertNotNull(result);
        verify(personalInfoRepository).save(any(PersonalInformation.class));
    }

    @Test
    void savePersonalInfo_NotOwner_ThrowsAccessDenied() {
        // Given
        Register otherRegister = new Register();
        otherRegister.setId(88L);
        testUser.setRegister(otherRegister);

        when(applicationRepository.findById(50L)).thenReturn(Optional.of(testApplication));

        // When & Then
        PersonalInfoDto infoDto = new PersonalInfoDto();
        assertThrows(AccessDeniedException.class, () ->
                userApplicationService.savePersonalInfo(50L, 99L, infoDto)
        );
    }

    @Test
    void submitApplication_Success() {
        // Given
        when(applicationRepository.findById(50L)).thenReturn(Optional.of(testApplication));
        doNothing().when(validationService).validateForSubmission(any(Application.class));
        when(applicationRepository.save(any(Application.class))).thenReturn(testApplication);

        // Mocking document repository used in mapToDto
        // Assuming mapToDto is called at the end of submitApplication
        if (testApplication.getPersonalInformation() != null) {
            when(documentRepository.findByPersonalInformation(any())).thenReturn(Collections.emptyList());
        }

        // When
        ApplicationDto result = userApplicationService.submitApplication(50L, 99L);

        // Then
        assertEquals(ApplicationStatus.SUBMITTED, result.getStatus());
        assertNotNull(result.getSubmittedAt());
    }
}