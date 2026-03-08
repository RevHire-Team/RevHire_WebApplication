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

import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    // ---------- Register Page ----------

    @Test
    void showRegisterPage_ShouldReturnRegisterView() throws Exception {

        mockMvc.perform(get("/auth/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/register"))
                .andExpect(model().attributeExists("user"));
    }

    // ---------- Login Page ----------

    @Test
    void showLoginPage_ShouldReturnLoginView() throws Exception {

        mockMvc.perform(get("/auth/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/login"));
    }

    // ---------- Forgot Password Page ----------

    @Test
    void showForgotPasswordPage_ShouldReturnForgotPasswordView() throws Exception {

        mockMvc.perform(get("/auth/forgot-password"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/forgotpassword"));
    }

    // ---------- Register User ----------

    @Test
    void registerUser_ShouldRedirectToLogin_WhenSuccess() throws Exception {

        doNothing().when(authService).registerUser(any(User.class));

        mockMvc.perform(post("/auth/register")
                        .param("email", "test@example.com")
                        .param("password", "123456"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login"));
    }

    // ---------- Login EMPLOYER ----------

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
                .andExpect(redirectedUrl("/employer/dashboard/1"));
    }

    // ---------- Login JOB SEEKER ----------

    @Test
    void login_ShouldRedirectJobSeekerDashboard() throws Exception {

        User user = new User();
        user.setUserId(2L);
        user.setRole(Role.JOB_SEEKER);

        when(authService.login("job@test.com", "1234")).thenReturn(user);

        mockMvc.perform(post("/auth/login")
                        .param("email", "job@test.com")
                        .param("password", "1234"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/jobseeker/dashboard/2"));
    }

    // ---------- Login ADMIN ----------

    @Test
    void login_ShouldRedirectAdminDashboard() throws Exception {

        User user = new User();
        user.setUserId(3L);
        user.setRole(Role.ADMIN);

        when(authService.login("admin@test.com", "1234")).thenReturn(user);

        mockMvc.perform(post("/auth/login")
                        .param("email", "admin@test.com")
                        .param("password", "1234"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/dashboard"));
    }

    // ---------- Login Failure ----------

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

    // ---------- Reset Password ----------

    @Test
    void resetPassword_ShouldReturnLoginPage_WhenSuccess() throws Exception {

        doNothing().when(authService)
                .resetPassword("test@test.com", "answer", "new123");

        mockMvc.perform(post("/auth/forgot-password")
                        .param("email", "test@test.com")
                        .param("answer", "answer")
                        .param("newPassword", "new123"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/login"))
                .andExpect(model().attributeExists("success"));
    }

    // ---------- Show Reset Password Page ----------

    @Test
    void showResetPasswordPage_ShouldReturnPage_WhenLoggedIn() throws Exception {

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("userId", 1L);

        mockMvc.perform(get("/auth/reset-password").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/reset-password"));
    }

    // ---------- Update Password ----------

    @Test
    void updatePassword_ShouldRedirectBack() throws Exception {

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("userId", 1L);

        doNothing().when(authService)
                .updatePassword(1L, "old123", "new123");

        mockMvc.perform(post("/auth/update-password")
                        .session(session)
                        .param("currentPassword", "old123")
                        .param("newPassword", "new123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/reset-password"));
    }

    // ---------- Delete Account ----------

    @Test
    void deleteAccount_ShouldRedirectHome_WhenDeleted() throws Exception {

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("userId", 1L);

        doNothing().when(authService).deleteUser(1L);

        mockMvc.perform(get("/auth/delete").session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }
}