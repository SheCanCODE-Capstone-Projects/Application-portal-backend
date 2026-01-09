//package com.igirerwanda.application_portal_backend.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.igirerwanda.application_portal_backend.admin.controller.AdminController;
//import com.igirerwanda.application_portal_backend.admin.dto.AdminCreateDto;
//import com.igirerwanda.application_portal_backend.admin.dto.AdminResponseDto;
//import com.igirerwanda.application_portal_backend.admin.service.AdminService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.web.servlet.MockMvc;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.when;
//import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@WebMvcTest(AdminController.class)
//public class AdminControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private AdminService adminService;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    private AdminCreateDto adminCreateDto;
//    private AdminResponseDto adminResponseDto;
//
//    @BeforeEach
//    void setUp() {
//        adminCreateDto = new AdminCreateDto();
//        adminCreateDto.setEmail("admin@example.com");
//        adminCreateDto.setFirstName("Alice");
//        adminCreateDto.setLastName("AdminUser");
//        adminCreateDto.setPhone("+250712345678");
//        adminCreateDto.setSetPassword("StrongP@ss123!");
//
//        adminResponseDto = new AdminResponseDto(1L, "admin@example.com", "ADMIN");
//    }
//
//    @Test
//    @WithMockUser(roles = {"SUPER_ADMIN"})
//    void createAdmin_WithSuperAdminRole_Success() throws Exception {
//        // Given
//        when(adminService.createAdmin(any(AdminCreateDto.class))).thenReturn(adminResponseDto);
//
//        // When & Then
//        mockMvc.perform(post("/api/v1/admin/users")
//                .with(csrf())
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(adminCreateDto)))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.id").value(1L))
//                .andExpect(jsonPath("$.email").value("admin@example.com"))
//                .andExpect(jsonPath("$.role").value("ADMIN"));
//    }
//
//    @Test
//    @WithMockUser(authorities = {"ADMIN_MANAGE"})
//    void createAdmin_WithAdminManageAuthority_Success() throws Exception {
//        // Given
//        when(adminService.createAdmin(any(AdminCreateDto.class))).thenReturn(adminResponseDto);
//
//        // When & Then
//        mockMvc.perform(post("/api/v1/admin/users")
//                .with(csrf())
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(adminCreateDto)))
//                .andExpect(status().isCreated());
//    }
//
//    @Test
//    @WithMockUser(roles = {"USER"})
//    void createAdmin_WithUserRole_Forbidden() throws Exception {
//        // When & Then
//        mockMvc.perform(post("/api/v1/admin/users")
//                .with(csrf())
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(adminCreateDto)))
//                .andExpect(status().isForbidden());
//    }
//
//    @Test
//    void createAdmin_Unauthenticated_Unauthorized() throws Exception {
//        // When & Then
//        mockMvc.perform(post("/api/v1/admin/users")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(adminCreateDto)))
//                .andExpect(status().isUnauthorized());
//    }
//}