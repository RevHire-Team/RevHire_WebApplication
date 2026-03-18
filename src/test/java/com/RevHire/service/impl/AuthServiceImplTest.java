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

    // ====================== REGISTER ======================
    @Test
    void testRegisterUserSuccess() {
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
        when(userRepository.save(user)).thenReturn(user);

        User result = authService.registerUser(user);

        assertNotNull(result);
        assertEquals("test@gmail.com", result.getEmail());
        verify(userRepository).save(user);
    }

    @Test
    void testRegisterUserEmailAlreadyExists() {
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authService.registerUser(user));

        assertEquals("Email already registered", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    // ====================== LOGIN ======================
    @Test
    void testLoginSuccess() {
        when(userRepository.findByEmail("test@gmail.com")).thenReturn(Optional.of(user));

        User result = authService.login("test@gmail.com", "12345");

        assertNotNull(result);
        assertEquals("test@gmail.com", result.getEmail());
    }

    @Test
    void testLoginInvalidCredentials() {
        when(userRepository.findByEmail("test@gmail.com")).thenReturn(Optional.of(user));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authService.login("test@gmail.com", "wrongpassword"));

        assertEquals("Invalid credentials", exception.getMessage());
    }

    @Test
    void testLoginUserNotFound() {
        when(userRepository.findByEmail("unknown@gmail.com")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authService.login("unknown@gmail.com", "12345"));

        assertEquals("Invalid credentials", exception.getMessage());
    }

    // ====================== RESET PASSWORD ======================
    @Test
    void testResetPasswordSuccess() {
        when(userRepository.findByEmail("test@gmail.com")).thenReturn(Optional.of(user));

        String result = authService.resetPassword("test@gmail.com", "pet", "newpassword");

        assertEquals("Password updated successfully", result);
        assertEquals("newpassword", user.getPasswordHash());
        verify(userRepository).save(user);
    }

    @Test
    void testResetPasswordWrongSecurityAnswer() {
        when(userRepository.findByEmail("test@gmail.com")).thenReturn(Optional.of(user));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authService.resetPassword("test@gmail.com", "wronganswer", "newpassword"));

        assertEquals("Security answer incorrect", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void testResetPasswordUserNotFound() {
        when(userRepository.findByEmail("unknown@gmail.com")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authService.resetPassword("unknown@gmail.com", "pet", "newpassword"));

        assertEquals("User not found", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    // ====================== UPDATE PASSWORD ======================
    @Test
    void testUpdatePasswordSuccess() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        authService.updatePassword(1L, "12345", "newpass");

        assertEquals("newpass", user.getPasswordHash());
        verify(userRepository).save(user);
    }

    @Test
    void testUpdatePasswordIncorrectCurrentPassword() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authService.updatePassword(1L, "wrongpass", "newpass"));

        assertEquals("The current password you entered is incorrect", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void testUpdatePasswordUserNotFound() {
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authService.updatePassword(2L, "12345", "newpass"));

        assertEquals("User session expired or user not found", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    // ====================== DELETE USER ======================
    @Test
    void testDeleteUserSuccess() {
        when(userRepository.existsById(1L)).thenReturn(true);

        authService.deleteUser(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    void testDeleteUserNotFound() {
        when(userRepository.existsById(1L)).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authService.deleteUser(1L));

        assertEquals("User not found", exception.getMessage());
        verify(userRepository, never()).deleteById(any());
    }
}