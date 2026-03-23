package com.RevHire.controller;

import com.RevHire.entity.Role;
import com.RevHire.entity.User;
import com.RevHire.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Test
    void showRegisterPage_ShouldReturnRegisterView() throws Exception {
        mockMvc.perform(get("/auth/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/register"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    void showLoginPage_ShouldReturnLoginView() throws Exception {
        mockMvc.perform(get("/auth/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/login"));
    }

    @Test
    void showForgotPasswordPage_ShouldReturnView() throws Exception {
        mockMvc.perform(get("/auth/forgot-password"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/forgotpassword"));
    }

    @Test
    void registerUser_ShouldRedirectToLogin_WhenSuccess() throws Exception {
        when(authService.registerUser(any(User.class))).thenReturn(new User());

        mockMvc.perform(post("/auth/register")
                        .flashAttr("user", new User()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login"));
    }

    @Test
    void login_ShouldRedirectEmployerDashboard() throws Exception {
        User user = new User();
        user.setUserId(1L);
        user.setRole(Role.EMPLOYER);

        when(authService.login("emp@test.com", "1234")).thenReturn(user);

        mockMvc.perform(post("/auth/login")
                        .param("email", "emp@test.com")
                        .param("password", "1234"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/employer/dashboard/1"))
                .andExpect(result -> {
                    Object role = result.getRequest().getSession().getAttribute("role");
                    assertNotNull(role);
                    assertEquals("EMPLOYER", role.toString());
                });
    }

    @Test
    void login_ShouldRedirectJobSeekerDashboard() throws Exception {
        User user = new User();
        user.setUserId(2L);
        user.setRole(Role.JOB_SEEKER);

        when(authService.login("user@test.com", "1234")).thenReturn(user);

        mockMvc.perform(post("/auth/login")
                        .param("email", "user@test.com")
                        .param("password", "1234"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/jobseeker/dashboard/2"));
    }

    @Test
    void login_ShouldReturnLoginPage_WhenInvalidCredentials() throws Exception {
        when(authService.login(anyString(), anyString()))
                .thenThrow(new RuntimeException("Invalid credentials"));

        mockMvc.perform(post("/auth/login")
                        .param("email", "wrong@test.com")
                        .param("password", "wrong"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/login"))
                .andExpect(model().attributeExists("error"));
    }

    @Test
    void resetPassword_ShouldReturnLoginPage_WhenSuccess() throws Exception {
        when(authService.resetPassword(anyString(), anyString(), anyString()))
                .thenReturn("Password updated successfully");

        mockMvc.perform(post("/auth/forgot-password")
                        .param("email", "test@test.com")
                        .param("answer", "Blue")
                        .param("newPassword", "newPass123"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/login"))
                .andExpect(model().attributeExists("success"));
    }

    @Test
    void resetPassword_ShouldReturnForgotPage_WhenError() throws Exception {
        when(authService.resetPassword(anyString(), anyString(), anyString()))
                .thenThrow(new RuntimeException("Wrong answer"));

        mockMvc.perform(post("/auth/forgot-password")
                        .param("email", "test@test.com")
                        .param("answer", "wrong")
                        .param("newPassword", "newPass123"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/forgotpassword"))
                .andExpect(model().attributeExists("error"));
    }

    @Test
    void showResetPasswordPage_ShouldRedirect_WhenNotLoggedIn() throws Exception {
        mockMvc.perform(get("/auth/reset-password"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login"));
    }

    @Test
    void showResetPasswordPage_ShouldReturnView_WhenLoggedIn() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("userId", 1L);

        mockMvc.perform(get("/auth/reset-password").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/reset-password"));
    }

    @Test
    void updatePassword_ShouldRedirectWithSuccess() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("userId", 1L);

        doNothing().when(authService).updatePassword(anyLong(), anyString(), anyString());

        mockMvc.perform(post("/auth/update-password")
                        .session(session)
                        .param("currentPassword", "old")
                        .param("newPassword", "new"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("success"));
    }

    @Test
    void updatePassword_ShouldRedirectWithError() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("userId", 1L);

        doThrow(new RuntimeException("Incorrect current password"))
                .when(authService).updatePassword(anyLong(), anyString(), anyString());

        mockMvc.perform(post("/auth/update-password")
                        .session(session)
                        .param("currentPassword", "wrong")
                        .param("newPassword", "new"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("error", "Incorrect current password"));
    }

    @Test
    void updatePassword_ShouldRedirectToLogin_WhenNoSession() throws Exception {
        mockMvc.perform(post("/auth/update-password")
                        .param("currentPassword", "old")
                        .param("newPassword", "new"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login"));
    }

    @Test
    void deleteAccount_ShouldRedirectHome_WhenSuccess() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("userId", 100L);

        doNothing().when(authService).deleteUser(100L);

        mockMvc.perform(get("/auth/delete").session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attributeExists("success"));

        verify(authService).deleteUser(100L);
    }

    @Test
    void deleteAccount_ShouldRedirectReset_WhenError() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("userId", 100L);

        doThrow(new RuntimeException("Delete failed"))
                .when(authService).deleteUser(100L);

        mockMvc.perform(get("/auth/delete").session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/reset-password"))
                .andExpect(flash().attributeExists("error"));
    }

    @Test
    void deleteAccount_ShouldRedirectToLogin_WhenNoSession() throws Exception {
        mockMvc.perform(get("/auth/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login"));
    }
}