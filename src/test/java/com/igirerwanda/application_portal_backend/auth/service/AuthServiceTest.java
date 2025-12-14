package com.igirerwanda.application_portal_backend.auth.service;

import com.igirerwanda.application_portal_backend.auth.dto.LoginRequest;
import com.igirerwanda.application_portal_backend.auth.dto.LoginResponse;
import com.igirerwanda.application_portal_backend.auth.repository.UserRepository;
import com.igirerwanda.application_portal_backend.common.enums.UserRole;
import com.igirerwanda.application_portal_backend.common.enums.UserStatus;
import com.igirerwanda.application_portal_backend.config.JwtService;
import com.igirerwanda.application_portal_backend.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthServiceImpl authService;

    private User testUser;
    private LoginRequest loginRequest;
    private BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
        
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setPassword(passwordEncoder.encode("password123"));
        testUser.setRole(UserRole.USER);
        testUser.setStatus(UserStatus.ACTIVE);
        
        loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");
    }

    @Test
    void authenticate_ValidCredentialsAndActiveUser_ShouldReturnToken() {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(jwtService.generateToken(1L, "test@example.com", UserRole.USER)).thenReturn("jwt-token");
        
        // When
        LoginResponse response = authService.authenticate(loginRequest);
        
        // Then
        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        assertEquals(UserRole.USER, response.getRole());
        assertEquals(1L, response.getUserId());
    }

    @Test
    void authenticate_UnverifiedUser_ShouldThrowException() {
        // Given
        testUser.setStatus(UserStatus.PENDING_VERIFICATION);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        
        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> authService.authenticate(loginRequest));
        assertEquals("Please verify your email before logging in.", exception.getMessage());
    }

    @Test
    void authenticate_WrongPassword_ShouldThrowException() {
        // Given
        loginRequest.setPassword("wrongpassword");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        
        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> authService.authenticate(loginRequest));
        assertEquals("Invalid credentials", exception.getMessage());
    }

    @Test
    void authenticate_NonexistentUser_ShouldThrowException() {
        // Given
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());
        loginRequest.setEmail("nonexistent@example.com");
        
        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> authService.authenticate(loginRequest));
        assertEquals("Invalid credentials", exception.getMessage());
    }

    @Test
    void authenticate_ShouldNormalizeEmail() {
        // Given
        loginRequest.setEmail("  TEST@EXAMPLE.COM  ");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(jwtService.generateToken(anyLong(), anyString(), any())).thenReturn("jwt-token");
        
        // When
        authService.authenticate(loginRequest);
        
        // Then
        verify(userRepository).findByEmail("test@example.com");
    }
}