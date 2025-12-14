package com.igirerwanda.application_portal_backend.auth.service;

import com.igirerwanda.application_portal_backend.auth.entity.EmailVerificationToken;
import com.igirerwanda.application_portal_backend.auth.repository.EmailVerificationTokenRepository;
import com.igirerwanda.application_portal_backend.auth.repository.UserRepository;
import com.igirerwanda.application_portal_backend.common.enums.UserStatus;
import com.igirerwanda.application_portal_backend.notification.service.EmailService;
import com.igirerwanda.application_portal_backend.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailVerificationTokenRepository emailVerificationTokenRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private AuthServiceImpl authService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setStatus(UserStatus.PENDING_VERIFICATION);
    }

    @Test
    void resendVerificationEmail_UnverifiedUser_ShouldResend() {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // When
        authService.resendVerificationEmail("test@example.com");

        // Then
        verify(emailVerificationTokenRepository).deleteByUser(testUser);
        verify(emailVerificationTokenRepository).save(any(EmailVerificationToken.class));
        verify(emailService).sendVerificationEmail(eq("test@example.com"), anyString());
    }

    @Test
    void resendVerificationEmail_VerifiedUser_ShouldThrowException() {
        // Given
        testUser.setStatus(UserStatus.ACTIVE);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // When & Then
        assertThrows(RuntimeException.class, () -> authService.resendVerificationEmail("test@example.com"));
        verify(emailService, never()).sendVerificationEmail(anyString(), anyString());
    }

    @Test
    void resendVerificationEmail_NonexistentUser_ShouldReturnSilently() {
        // Given
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // When
        authService.resendVerificationEmail("nonexistent@example.com");

        // Then
        verify(emailService, never()).sendVerificationEmail(anyString(), anyString());
        verify(emailVerificationTokenRepository, never()).save(any());
    }

    @Test
    void resendVerificationEmail_ShouldNormalizeEmail() {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // When
        authService.resendVerificationEmail("  TEST@EXAMPLE.COM  ");

        // Then
        verify(userRepository).findByEmail("test@example.com");
        verify(emailService).sendVerificationEmail(eq("test@example.com"), anyString());
    }
}