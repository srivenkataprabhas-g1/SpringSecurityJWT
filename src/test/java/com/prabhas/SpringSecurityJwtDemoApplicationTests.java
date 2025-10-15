package com.prabhas;

import com.prabhas.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class SpringSecurityJwtDemoApplicationTests {

    @Autowired
    private UserService userService;

    @Test
    void contextLoads() {
        // Basic test to load Spring context
        assertThat(userService).isNotNull();
    }

    @Test
    void testCreateUser() {
        var roles = Set.of("ROLE_USER");
        var user = userService.createUser(
                "testuser",
                "testpassword",
                "testuser@example.com",
                "Test",
                "User",
                "1234567890",
                roles
        );
        assertThat(user).isNotNull();
        assertThat(user.getUsername()).isEqualTo("testuser");
        assertThat(user.getRoles()).hasSize(1);
        assertThat(user.getEmail()).isEqualTo("testuser@example.com");
    }
}
