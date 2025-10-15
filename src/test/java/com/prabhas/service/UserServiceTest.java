package com.prabhas.service;

import com.prabhas.models.entity.Role;
import com.prabhas.models.entity.User;
import com.prabhas.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleService roleService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private Role testRole;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedPassword");
        testUser.setEnabled(true);

        testRole = new Role();
        testRole.setId(1L);
        testRole.setRoleName("ROLE_USER");
        testRole.setDescription("User role");
    }

    @Test
    void createUser_WithValidData_ShouldCreateUserSuccessfully() {
        // Given
        String username = "newuser";
        String password = "password";
        String email = "newuser@example.com";
        Set<String> roleNames = Set.of("ROLE_USER");

        when(userRepository.existsByUsername(username)).thenReturn(false);
        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(passwordEncoder.encode(password)).thenReturn("encodedPassword");
        when(roleService.findRolesByNames(roleNames)).thenReturn(Set.of(testRole));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        User result = userService.createUser(username, password, email, "First", "Last", "1234567890", roleNames);

        // Then
        assertNotNull(result);
        verify(userRepository).existsByUsername(username);
        verify(userRepository).existsByEmail(email);
        verify(passwordEncoder).encode(password);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUser_WithExistingUsername_ShouldThrowException() {
        // Given
        String username = "existinguser";
        when(userRepository.existsByUsername(username)).thenReturn(true);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.createUser(username, "password", "email@test.com", "First", "Last", "1234567890", Set.of("ROLE_USER"));
        });

        assertEquals("Username already exists: existinguser", exception.getMessage());
        verify(userRepository).existsByUsername(username);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getUserByUsername_WithValidUsername_ShouldReturnUser() {
        // Given
        String username = "testuser";
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUser));

        // When
        User result = userService.getUserByUsername(username);

        // Then
        assertNotNull(result);
        assertEquals(username, result.getUsername());
        verify(userRepository).findByUsername(username);
    }

    @Test
    void getUserByUsername_WithInvalidUsername_ShouldThrowException() {
        // Given
        String username = "nonexistent";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.getUserByUsername(username);
        });

        assertEquals("User not found: nonexistent", exception.getMessage());
        verify(userRepository).findByUsername(username);
    }

    @Test
    void addRoleToUser_WithValidData_ShouldAddRoleSuccessfully() {
        // Given
        String username = "testuser";
        String roleName = "ROLE_ADMIN";
        Role adminRole = new Role("ROLE_ADMIN", "Admin role");

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUser));
        when(roleService.getRoleByName(roleName)).thenReturn(adminRole);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        User result = userService.addRoleToUser(username, roleName);

        // Then
        assertNotNull(result);
        verify(userRepository).findByUsername(username);
        verify(roleService).getRoleByName(roleName);
        verify(userRepository).save(testUser);
    }

    @Test
    void changePassword_WithValidData_ShouldUpdatePassword() {
        // Given
        String username = "testuser";
        String newPassword = "newpassword";
        String encodedPassword = "encodedNewPassword";

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.encode(newPassword)).thenReturn(encodedPassword);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        User result = userService.changePassword(username, newPassword);

        // Then
        assertNotNull(result);
        verify(userRepository).findByUsername(username);
        verify(passwordEncoder).encode(newPassword);
        verify(userRepository).save(testUser);
    }
}
