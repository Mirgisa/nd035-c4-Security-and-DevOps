package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {
    private UserController userController;
    private UserRepository userRepo = mock(UserRepository.class);
    private CartRepository cartRepo = mock(CartRepository.class);
    private BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);

    @Before
    public void setUp(){
        userController  = new UserController();
        TestUtils.injectObjects(userController, "userRepository", userRepo);
        TestUtils.injectObjects(userController, "cartRepository", cartRepo);
        TestUtils.injectObjects(userController, "passwordEncoder", encoder);

    }
    @Test
    public void createUserTest() {
        when(encoder.encode("password123")).thenReturn("thisIsHashed");
        CreateUserRequest r = new CreateUserRequest();
        r.setUsername("testuser");
        r.setPassword("password123");
        r.setConfirmPassword("password123");

        final ResponseEntity<User> response = userController.createUser(r);
        assertNotNull(response);
        assertEquals(201, response.getStatusCodeValue());
        User user = response.getBody();
        assertNotNull(user);
        assertEquals(0,user.getId());
        assertEquals("testuser", user.getUsername());
        assertEquals("thisIsHashed", user.getPassword());
        // Implement test logic here
    }
    @Test
    public void getUserByUserNameTest(){
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        when(userRepo.findByUsername("testuser")).thenReturn(user);

        final ResponseEntity<User> response = userController.findByUsername("testuser");
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        User foundUser = response.getBody();
        assertNotNull(foundUser);
        assertEquals("testuser", foundUser.getUsername());
    }
    @Test
    public void getUserByIdTest(){
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        when(userRepo.findById(1L)).thenReturn(java.util.Optional.of(user));

        final ResponseEntity<User> response = userController.findById(1L);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        User foundUser = response.getBody();
        assertNotNull(foundUser);
        assertEquals("testuser", foundUser.getUsername());
    }
}
