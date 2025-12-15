package com.igirerwanda.application_portal_backend.service;

import com.igirerwanda.application_portal_backend.admin.dto.AdminCreateDto;
import com.igirerwanda.application_portal_backend.admin.dto.AdminResponseDto;
import com.igirerwanda.application_portal_backend.admin.service.AdminServiceImpl;
import com.igirerwanda.application_portal_backend.common.enums.UserRole;
import com.igirerwanda.application_portal_backend.common.exception.DuplicateResourceException;
import com.igirerwanda.application_portal_backend.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdminServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @InjectMocks
    private AdminServiceImpl adminService;
    
    private AdminCreateDto adminCreateDto;
    
    @BeforeEach
    void setUp() {
        adminCreateDto = new AdminCreateDto();
        adminCreateDto.setEmail("admin@example.com");
        adminCreateDto.setFirstName("Alice");
        adminCreateDto.setLastName("AdminUser");
        adminCreateDto.setPhone("+250712345678");
        adminCreateDto.setSetPassword("StrongP@ss123!");
    }
    
    @Test
    void createAdmin_Success() {
        // Given
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        
        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setEmail("admin@example.com");
        savedUser.setRole(UserRole.ADMIN);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        
        // When
        AdminResponseDto result = adminService.createAdmin(adminCreateDto);
        
        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("admin@example.com", result.getEmail());
        assertEquals("ADMIN", result.getRole());
        
        verify(userRepository).existsByEmail("admin@example.com");
        verify(passwordEncoder).encode("StrongP@ss123!");
        verify(userRepository).save(any(User.class));
    }
    
    @Test
    void createAdmin_DuplicateEmail_ThrowsException() {
        // Given
        when(userRepository.existsByEmail(anyString())).thenReturn(true);
        
        // When & Then
        assertThrows(DuplicateResourceException.class, () -> {
            adminService.createAdmin(adminCreateDto);
        });
        
        verify(userRepository).existsByEmail("admin@example.com");
        verify(userRepository, never()).save(any(User.class));
    }
}