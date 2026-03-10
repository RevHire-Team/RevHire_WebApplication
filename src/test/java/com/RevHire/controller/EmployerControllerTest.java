package com.RevHire.controller;

import com.RevHire.dto.EmployerDashboardDTO;
import com.RevHire.dto.EmployerProfileDTO;
import com.RevHire.entity.User;
import com.RevHire.service.EmployerService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class EmployerControllerTest {

    private MockMvc mockMvc;

    @Mock
    private EmployerService employerService;

    @InjectMocks
    private EmployerController employerController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(employerController).build();
        objectMapper = new ObjectMapper();
    }

    // ===============================
    // TEST CREATE OR UPDATE PROFILE
    // ===============================

    @Test
    void testCreateOrUpdateProfile() throws Exception {

        EmployerProfileDTO dto = new EmployerProfileDTO();
        dto.setCompanyName("Test Company");

        when(employerService.createOrUpdateProfile(1L, dto)).thenReturn(dto);

        mockMvc.perform(post("/employer/profile/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    // ===============================
    // TEST EDIT PROFILE PAGE
    // ===============================

    @Test
    void testShowEditProfilePage() throws Exception {

        User user = new User();
        user.setUserId(1L);

        EmployerProfileDTO profile = new EmployerProfileDTO();
        profile.setCompanyName("ABC Pvt Ltd");

        when(employerService.getProfile(1L)).thenReturn(profile);

        mockMvc.perform(get("/employer/profile/edit")
                        .sessionAttr("loggedInUser", user))
                .andExpect(status().isOk())
                .andExpect(view().name("employer/edit-company-profile"))
                .andExpect(model().attributeExists("profile"))
                .andExpect(model().attributeExists("userId"));
    }

    // ===============================
    // TEST EDIT PROFILE REDIRECT
    // ===============================

    @Test
    void testShowEditProfilePage_NotLoggedIn() throws Exception {

        mockMvc.perform(get("/employer/profile/edit"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login"));
    }

    // ===============================
    // TEST VIEW PROFILE PAGE
    // ===============================

    @Test
    void testViewProfilePage() throws Exception {

        User user = new User();
        user.setUserId(1L);

        EmployerProfileDTO profile = new EmployerProfileDTO();
        profile.setCompanyName("XYZ Pvt Ltd");

        when(employerService.getProfile(1L)).thenReturn(profile);

        mockMvc.perform(get("/employer/profile")
                        .sessionAttr("loggedInUser", user))
                .andExpect(status().isOk())
                .andExpect(view().name("employer/company-profile"))
                .andExpect(model().attributeExists("profile"));
    }

    // ===============================
    // TEST VIEW PROFILE REDIRECT
    // ===============================

    @Test
    void testViewProfilePage_NotLoggedIn() throws Exception {

        mockMvc.perform(get("/employer/profile"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login"));
    }

    // ===============================
    // TEST DASHBOARD
    // ===============================

    @Test
    void testShowDashboard() throws Exception {

        EmployerDashboardDTO dashboard = new EmployerDashboardDTO();

        when(employerService.getDashboard(1L)).thenReturn(dashboard);

        mockMvc.perform(get("/employer/dashboard/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("employer/dashboard"))
                .andExpect(model().attributeExists("dashboard"));
    }

}