package com.RevHire.service;

import com.RevHire.entity.Role;
import com.RevHire.entity.User;
import com.RevHire.repository.UserRepository;
import com.RevHire.service.impl.AuthServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthServiceImpl authService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterUser() {

        User user = new User();
        user.setEmail("test@gmail.com");
        user.setPasswordHash("1234");
        user.setRole(Role.JOB_SEEKER);

        when(userRepository.existsByEmail("test@gmail.com")).thenReturn(false);
        when(userRepository.save(user)).thenReturn(user);

        User savedUser = authService.registerUser(user);

        assertNotNull(savedUser);
        verify(userRepository,times(1)).save(user);
    }

    @Test
    void testLoginSuccess() {

        User user = new User();
        user.setEmail("test@gmail.com");
        user.setPasswordHash("1234");

        when(userRepository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.of(user));

        User result = authService.login("test@gmail.com","1234");

        assertEquals("test@gmail.com",result.getEmail());
    }

    @Test
    void testResetPassword() {

        User user = new User();
        user.setEmail("test@gmail.com");
        user.setSecurityAnswerHash("dog");

        when(userRepository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.of(user));

        String result = authService.resetPassword(
                "test@gmail.com","dog","newpass"
        );

        assertEquals("Password updated successfully",result);
    }
}