package com.prabhas.controller;

import com.prabhas.service.RoleService;
import com.prabhas.service.UserService;
import com.prabhas.models.entity.Role;
import com.prabhas.models.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RoleController.class)
class RoleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RoleService roleService;

    @MockBean
    private UserService userService;

    private Role testRole;
    private User testUser;

    @BeforeEach
    void setUp() {
        testRole = new Role();
        testRole.setId(1L);
        testRole.setRoleName("ROLE_USER");
        testRole.setDescription("User role");

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
    }

    @Test
    @WithMockUser(roles = "USER")
    void userProfile_WithUserRole_ShouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/roles/profile"))
                .andExpect(status().isOk())
                .andExpect(content().string("Access granted to USER or ADMIN"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void userProfile_WithAdminRole_ShouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/roles/profile"))
                .andExpect(status().isOk())
                .andExpect(content().string("Access granted to USER or ADMIN"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void userData_WithUserRole_ShouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/roles/user/data"))
                .andExpect(status().isOk())
                .andExpect(content().string("Sensitive data for ROLE_USER"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void userData_WithAdminRole_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/api/roles/user/data"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminData_WithAdminRole_ShouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/roles/admin/data"))
                .andExpect(status().isOk())
                .andExpect(content().string("Sensitive data for ROLE_ADMIN"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void adminData_WithUserRole_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/api/roles/admin/data"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllRoles_WithAdminRole_ShouldReturnRoles() throws Exception {
        List<Role> roles = Arrays.asList(testRole);
        when(roleService.getAllRoles()).thenReturn(roles);

        mockMvc.perform(get("/api/roles/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].roleName").value("ROLE_USER"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAllRoles_WithUserRole_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/api/roles/all"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createRole_WithAdminRole_ShouldCreateRole() throws Exception {
        when(roleService.createRole(anyString(), anyString())).thenReturn(testRole);

        mockMvc.perform(post("/api/roles/create")
                .with(csrf())
                .param("roleName", "ROLE_TEST")
                .param("description", "Test role"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roleName").value("ROLE_USER"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void createRole_WithUserRole_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(post("/api/roles/create")
                .with(csrf())
                .param("roleName", "ROLE_TEST")
                .param("description", "Test role"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getAllRoles_WithoutAuthentication_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/api/roles/all"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteRole_WithAdminRole_ShouldDeleteRole() throws Exception {
        mockMvc.perform(delete("/api/roles/ROLE_TEST")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Role 'ROLE_TEST' deleted successfully"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void addRoleToUser_WithAdminRole_ShouldAddRole() throws Exception {
        when(userService.addRoleToUser(anyString(), anyString())).thenReturn(testUser);

        mockMvc.perform(post("/api/roles/user/testuser/add-role")
                .with(csrf())
                .param("roleName", "ROLE_ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"));
    }
}