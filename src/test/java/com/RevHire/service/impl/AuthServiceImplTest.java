package com.RevHire.service.impl;

import com.RevHire.entity.User;
import com.RevHire.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthServiceImpl authService;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setUserId(1L);
        user.setEmail("test@gmail.com");
        user.setPasswordHash("12345");
        user.setSecurityAnswerHash("pet");
    }

    @Test
    void testRegisterUserSuccess() {

        when(userRepository.existsByEmail(user.getEmail()))
                .thenReturn(false);

        when(userRepository.save(user))
                .thenReturn(user);

        User result = authService.registerUser(user);

        assertNotNull(result);
        assertEquals("test@gmail.com", result.getEmail());

        verify(userRepository).save(user);
    }

    @Test
    void testRegisterUserEmailAlreadyExists() {

        when(userRepository.existsByEmail(user.getEmail()))
                .thenReturn(true);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> authService.registerUser(user)
        );

        assertEquals("Email already registered", exception.getMessage());
    }

    @Test
    void testLoginSuccess() {

        when(userRepository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.of(user));

        User result = authService.login("test@gmail.com", "12345");

        assertNotNull(result);
        assertEquals("test@gmail.com", result.getEmail());
    }

    @Test
    void testLoginInvalidCredentials() {

        when(userRepository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.of(user));

        assertThrows(RuntimeException.class,
                () -> authService.login("test@gmail.com", "wrongpassword"));
    }

    @Test
    void testResetPasswordSuccess() {

        when(userRepository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.of(user));

        String result = authService.resetPassword(
                "test@gmail.com",
                "pet",
                "newpassword"
        );

        assertEquals("Password updated successfully", result);

        verify(userRepository).save(user);
    }

    @Test
    void testResetPasswordWrongSecurityAnswer() {

        when(userRepository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.of(user));

        assertThrows(RuntimeException.class,
                () -> authService.resetPassword(
                        "test@gmail.com",
                        "wronganswer",
                        "newpassword"));
    }

    @Test
    void testUpdatePasswordSuccess() {

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        authService.updatePassword(1L, "12345", "newpass");

        verify(userRepository).save(user);
        assertEquals("newpass", user.getPasswordHash());
    }

    @Test
    void testUpdatePasswordIncorrectCurrentPassword() {

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        assertThrows(RuntimeException.class,
                () -> authService.updatePassword(1L, "wrongpass", "newpass"));
    }

    @Test
    void testDeleteUserSuccess() {

        when(userRepository.existsById(1L))
                .thenReturn(true);

        authService.deleteUser(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    void testDeleteUserNotFound() {

        when(userRepository.existsById(1L))
                .thenReturn(false);

        assertThrows(RuntimeException.class,
                () -> authService.deleteUser(1L));
    }
}