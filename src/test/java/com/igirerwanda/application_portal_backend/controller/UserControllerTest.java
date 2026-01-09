package com.igirerwanda.application_portal_backend.controller;

import com.igirerwanda.application_portal_backend.user.controller.UserController;
import com.igirerwanda.application_portal_backend.user.dto.UserResponseDto;
import com.igirerwanda.application_portal_backend.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private SimpMessagingTemplate messagingTemplate;

    @Test
    @WithMockUser(username = "test@example.com")
    void getMyProfile_Success() throws Exception {
        UserResponseDto dto = new UserResponseDto();
        dto.setId(1L);
        dto.setEmail("test@example.com");
        dto.setUsername("testuser");

        when(userService.getCurrentUserProfile()).thenReturn(dto);

        mockMvc.perform(get("/api/v1/users/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }
}