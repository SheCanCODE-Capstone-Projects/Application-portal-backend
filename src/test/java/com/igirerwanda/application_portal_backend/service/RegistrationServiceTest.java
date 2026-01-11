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
        RegisterRequest req = new RegisterRequest();
        req.setEmail("new@test.com");
        req.setUsername("newuser");
        req.setPassword("pass");

        when(registerRepo.findByEmail(any())).thenReturn(Optional.empty());
        when(registerRepo.findByUsername(any())).thenReturn(Optional.empty());
        when(encoder.encode(any())).thenReturn("encoded");
        when(registerRepo.save(any(Register.class))).thenAnswer(i -> i.getArguments()[0]);

        registrationService.register(req);

        verify(registerRepo).save(any(Register.class));
        verify(tokenRepo).save(any(EmailVerificationToken.class));
    }

    @Test
    void register_DuplicateEmail_ThrowsException() {
        RegisterRequest req = new RegisterRequest();
        req.setEmail("exists@test.com");

        when(registerRepo.findByEmail(any())).thenReturn(Optional.of(new Register()));

        assertThrows(DuplicateResourceException.class, () -> registrationService.register(req));
    }
}