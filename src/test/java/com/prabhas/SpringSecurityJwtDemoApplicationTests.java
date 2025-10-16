package com.prabhas;

import com.prabhas.model.User;                  
import com.prabhas.repository.UserRepository;   
import com.prabhas.service.UserService;         

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;
import java.util.Optional;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SpringSecurityJwtDemoApplicationTests {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    static String username = "ci_testuser";
    static String email = "ci_testuser@example.com";

    @Test
    @Order(1)
    void testAddUser() {
        if(userRepository.findByUsername(username)!=null)
        {userRepository.deleteByUsername(username);}
        Set<String> roles = Set.of("ROLE_USER");
        User user = userService.createUser(
                username,
                "testpassword",
                email,
                "First",
                "Last",
                "9876543210",
                roles
        );
        assertNotNull(user);
        assertEquals(username, user.getUsername());
    }

    @Test
    @Order(2)
    void testUpdateUser() {
        User user = userRepository.findByUsername(username).orElseThrow();
        user.setFirstName("Changed");
        user.setLastName("Name");
        User updated = userRepository.save(user);
        assertThat(updated.getFirstName()).isEqualTo("Changed");
        assertThat(updated.getLastName()).isEqualTo("Name");
    }

    @Test
    @Order(3)
    void testSearchUser() {
        Optional<User> userOpt = userRepository.findByUsername(username);
        assertTrue(userOpt.isPresent());
        assertEquals(username, userOpt.get().getUsername());
    }

    @Test
    @Order(4)
    void testDeleteUser() {
        userRepository.deleteByUsername(username);
        Optional<User> deleted = userRepository.findByUsername(username);
        assertTrue(deleted.isEmpty());
    }
}
