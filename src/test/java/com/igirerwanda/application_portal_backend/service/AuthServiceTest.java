package com.igirerwanda.application_portal_backend.service;

import com.igirerwanda.application_portal_backend.auth.dto.LoginRequest;
import com.igirerwanda.application_portal_backend.auth.dto.LoginResponse;
import com.igirerwanda.application_portal_backend.auth.dto.RegisterRequest;
import com.igirerwanda.application_portal_backend.auth.service.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private RegistrationService registrationService;
    @Mock private LoginService loginService;
    @Mock private EmailVerificationService emailVerificationService;
    @Mock private PasswordResetService passwordResetService;

    @InjectMocks
    private AuthService authService;

    @Test
    void register_DelegatesToRegistrationService() {
        RegisterRequest req = new RegisterRequest();
        when(registrationService.register(req)).thenReturn(Map.of("msg", "success"));

        authService.register(req);
        verify(registrationService).register(req);
    }

    @Test
    void login_DelegatesToLoginService() {
        LoginRequest req = new LoginRequest("a", "b");
        when(loginService.login(req)).thenReturn(new LoginResponse("token"));

        LoginResponse res = authService.login(req);
        assertEquals("token", res.getToken());
    }
}