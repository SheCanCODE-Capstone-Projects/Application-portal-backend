package com.igirerwanda.application_portal_backend.service;

import com.igirerwanda.application_portal_backend.auth.dto.RegisterRequest;
import com.igirerwanda.application_portal_backend.auth.entity.EmailVerificationToken;
import com.igirerwanda.application_portal_backend.auth.entity.Register;
import com.igirerwanda.application_portal_backend.auth.repository.EmailVerificationTokenRepository;
import com.igirerwanda.application_portal_backend.auth.repository.RegisterRepository;
import com.igirerwanda.application_portal_backend.auth.service.RegistrationService;
import com.igirerwanda.application_portal_backend.auth.service.UserPromotionService;
import com.igirerwanda.application_portal_backend.common.exception.DuplicateResourceException;
import com.igirerwanda.application_portal_backend.notification.service.EmailService;
import com.igirerwanda.application_portal_backend.notification.service.WebSocketService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistrationServiceTest {

    @Mock private RegisterRepository registerRepo;
    @Mock private EmailVerificationTokenRepository tokenRepo;
    @Mock private PasswordEncoder encoder;
    @Mock private EmailService emailService;
    @Mock private WebSocketService webSocketService;
    @Mock private UserPromotionService userPromotionService;

    @InjectMocks
    private RegistrationService registrationService;

    @Test
    void register_Success() {
        // Given
        RegisterRequest request = new RegisterRequest();
        request.setEmail("new@user.com");
        request.setUsername("newuser");
        request.setPassword("password123");

        when(registerRepo.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(registerRepo.findByUsername(request.getUsername())).thenReturn(Optional.empty());
        when(encoder.encode(request.getPassword())).thenReturn("encodedPass");
        when(registerRepo.save(any(Register.class))).thenAnswer(i -> i.getArguments()[0]);

        // When
        registrationService.register(request);

        // Then
        verify(registerRepo).save(any(Register.class));
        verify(tokenRepo).save(any(EmailVerificationToken.class));
        verify(emailService).sendEmail(eq("new@user.com"), anyString(), anyString());
        verify(webSocketService).broadcastToAdmin(eq("users"), any());
    }

    @Test
    void register_DuplicateEmail_ThrowsException() {
        // Given
        RegisterRequest request = new RegisterRequest();
        request.setEmail("existing@user.com");

        when(registerRepo.findByEmail(request.getEmail())).thenReturn(Optional.of(new Register()));

        // When & Then
        assertThrows(DuplicateResourceException.class, () -> registrationService.register(request));
        verify(registerRepo, never()).save(any());
    }
}