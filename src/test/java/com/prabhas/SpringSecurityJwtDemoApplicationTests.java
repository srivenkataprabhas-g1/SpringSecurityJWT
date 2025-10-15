package com.prabhas;

import java.util.Set;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.prabhas.model.User;
import com.prabhas.repository.UserRepository;
import com.prabhas.service.UserService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class) // Ensures test order for demo
class SpringSecurityJwtDemoApplicationTests {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    static String username = "testuser_ci";
    static String email = "testuser_ci@example.com";

    @Test
    @Order(1)
    void testAddUser() {
        userRepository.deleteByUsername(username); // Ensure fresh test run
        Set<String> roles = Set.of("ROLE_USER");

        User user = userService.createUser(
                username,
                "testpassword",
                email,
                "Test",
                "User",
                "1234567890",
                roles
        );
        assertNotNull(user);
        assertThat(user.getUsername()).isEqualTo(username);
        assertThat(user.getEmail()).isEqualTo(email);
    }

    @Test
    @Order(2)
    void testUpdateUser() {
        User user = userRepository.findByUsername(username).orElseThrow();

        user.setFirstName("UpdatedFirst");
        user.setLastName("UpdatedLast");
        User updatedUser = userRepository.save(user);

        assertThat(updatedUser.getFirstName()).isEqualTo("UpdatedFirst");
        assertThat(updatedUser.getLastName()).isEqualTo("UpdatedLast");
    }

    @Test
    @Order(3)
    void testSearchUser() {
        User user = userRepository.findByUsername(username).orElse(null);
        assertNotNull(user);
        assertThat(user.getUsername()).isEqualTo(username);
    }

    @Test
    @Order(4)
    void testDeleteUser() {
        userRepository.deleteByUsername(username);

        var result = userRepository.findByUsername(username);
        assertThat(result).isEmpty();
    }
}
