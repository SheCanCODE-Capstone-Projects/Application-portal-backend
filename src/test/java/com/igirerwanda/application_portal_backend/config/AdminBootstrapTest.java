package com.igirerwanda.application_portal_backend.config;

import com.igirerwanda.application_portal_backend.common.enums.UserRole;
import com.igirerwanda.application_portal_backend.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdminBootstrapTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @InjectMocks
    private AdminBootstrap adminBootstrap;
    
    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(adminBootstrap, "bootstrapEmail", "admin@test.com");
        ReflectionTestUtils.setField(adminBootstrap, "bootstrapPassword", "TestPassword123!");
        ReflectionTestUtils.setField(adminBootstrap, "bootstrapEnabled", true);
    }
    
    @Test
    void run_NoAdminExists_CreatesDefaultAdmin() throws Exception {
        // Given
        when(userRepository.existsByRole(UserRole.ADMIN)).thenReturn(false);
        when(userRepository.existsByRole(UserRole.SUPER_ADMIN)).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        
        // When
        adminBootstrap.run();
        
        // Then
        verify(userRepository).existsByRole(UserRole.ADMIN);
        verify(userRepository).existsByRole(UserRole.SUPER_ADMIN);
        verify(passwordEncoder).encode("TestPassword123!");
        verify(userRepository).save(any(User.class));
    }
    
    @Test
    void run_AdminExists_SkipsCreation() throws Exception {
        // Given
        when(userRepository.existsByRole(UserRole.ADMIN)).thenReturn(true);
        
        // When
        adminBootstrap.run();
        
        // Then
        verify(userRepository).existsByRole(UserRole.ADMIN);
        verify(userRepository, never()).save(any(User.class));
    }
    
    @Test
    void run_BootstrapDisabled_SkipsCreation() throws Exception {
        // Given
        ReflectionTestUtils.setField(adminBootstrap, "bootstrapEnabled", false);
        
        // When
        adminBootstrap.run();
        
        // Then
        verify(userRepository, never()).existsByRole(any());
        verify(userRepository, never()).save(any(User.class));
    }
    
    @Test
    void run_NoPassword_SkipsCreation() throws Exception {
        // Given
        ReflectionTestUtils.setField(adminBootstrap, "bootstrapPassword", "");
        when(userRepository.existsByRole(UserRole.ADMIN)).thenReturn(false);
        when(userRepository.existsByRole(UserRole.SUPER_ADMIN)).thenReturn(false);
        
        // When
        adminBootstrap.run();
        
        // Then
        verify(userRepository, never()).save(any(User.class));
    }
}