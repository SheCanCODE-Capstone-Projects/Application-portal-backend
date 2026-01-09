package com.igirerwanda.application_portal_backend.service;

import com.igirerwanda.application_portal_backend.common.enums.UserStatus;
import com.igirerwanda.application_portal_backend.user.entity.User;
import com.igirerwanda.application_portal_backend.user.repository.UserRepository;
import com.igirerwanda.application_portal_backend.user.service.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void testSoftDelete_Success() {
        // 1. Use Long instead of UUID to match your User entity
        Long userId = 1L;

        User mockUser = new User();
        mockUser.setId(userId);
        mockUser.setStatus(UserStatus.PENDING_VERIFICATION); // Set an initial status

        // 2. Mock the repository behavior
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);

        // 3. Call the service
        User result = userService.softDelete(userId);

        // 4. Verify the status was changed to DISABLED
        assertEquals(UserStatus.DISABLED, result.getStatus());
        verify(userRepository).save(mockUser);
    }
}