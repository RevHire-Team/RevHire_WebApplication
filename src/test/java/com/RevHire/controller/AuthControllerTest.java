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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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

    // ---------- Navigation Tests ----------

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

    // ---------- Registration Tests ----------

    @Test
    void registerUser_ShouldRedirectToLogin_WhenSuccess() throws Exception {
        User mockUser = new User();
        when(authService.registerUser(any(User.class))).thenReturn(mockUser);

        mockMvc.perform(post("/auth/register")
                        .flashAttr("user", new User())
                        .param("email", "test@example.com")
                        .param("password", "123456"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login"));
    }

    // ---------- Login Role-Based Redirects ----------

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
                    // result.getRequest() returns the MockHttpServletRequest
                    Object role = result.getRequest().getSession().getAttribute("role");
                    assertNotNull(role);
                    assertEquals("EMPLOYER", role.toString());
                });
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

    // ---------- Password Management ----------

    @Test
    void resetPassword_ShouldReturnLoginPage_WhenSuccess() throws Exception {
        mockMvc.perform(post("/auth/forgot-password")
                        .param("email", "test@test.com")
                        .param("answer", "Blue")
                        .param("newPassword", "newPass123"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/login"))
                .andExpect(model().attributeExists("success"));

        verify(authService).resetPassword(eq("test@test.com"), anyString(), anyString());
    }

    @Test
    void showResetPasswordPage_ShouldRedirectToLogin_WhenNotLoggedIn() throws Exception {
        // No session provided
        mockMvc.perform(get("/auth/reset-password"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login"));
    }

    @Test
    void updatePassword_ShouldSetFlashError_WhenCurrentPasswordWrong() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("userId", 1L);

        doThrow(new RuntimeException("Incorrect current password"))
                .when(authService).updatePassword(anyLong(), anyString(), anyString());

        mockMvc.perform(post("/auth/update-password")
                        .session(session)
                        .param("currentPassword", "wrong")
                        .param("newPassword", "new123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("error", "Incorrect current password"));
    }

    // ---------- Account Deletion ----------

    @Test
    void deleteAccount_ShouldRedirectHome_WhenDeletedSuccessfully() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("userId", 100L);

        mockMvc.perform(get("/auth/delete").session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attributeExists("success"));

        verify(authService).deleteUser(100L);
    }

    @Test
    void deleteAccount_ShouldRedirectToLogin_WhenNoSession() throws Exception {
        mockMvc.perform(get("/auth/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login"));
    }
}