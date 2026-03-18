package com.RevHire.controller;

import com.RevHire.dto.EmployerDashboardDTO;
import com.RevHire.dto.EmployerProfileDTO;
import com.RevHire.entity.User;
import com.RevHire.service.EmployerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployerController.class)
class EmployerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployerService employerService;

    @Autowired
    private ObjectMapper objectMapper;

    private User mockUser;
    private MockHttpSession session;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setUserId(1L);
        mockUser.setEmail("employer@test.com");

        session = new MockHttpSession();
        session.setAttribute("loggedInUser", mockUser);
    }

    // ---------- Profile View/Edit Tests ----------

    @Test
    void viewProfilePage_ShouldReturnProfileView_WhenLoggedIn() throws Exception {
        EmployerProfileDTO profileDTO = new EmployerProfileDTO();
        profileDTO.setCompanyName("RevHire Corp");

        when(employerService.getProfile(1L)).thenReturn(profileDTO);

        mockMvc.perform(get("/employer/profile").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("employer/company-profile"))
                .andExpect(model().attribute("profile", profileDTO));
    }

    @Test
    void showEditProfilePage_ShouldRedirectToLogin_WhenNotLoggedIn() throws Exception {
        mockMvc.perform(get("/employer/profile/edit")) // No session
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login"));
    }

    @Test
    void showEditProfilePage_ShouldReturnEditView_WhenLoggedIn() throws Exception {
        when(employerService.getProfile(1L)).thenReturn(null); // Simulate new profile

        mockMvc.perform(get("/employer/profile/edit").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("employer/edit-company-profile"))
                .andExpect(model().attributeExists("profile"))
                .andExpect(model().attribute("userId", 1L));
    }

    // ---------- API / JSON Tests ----------

    @Test
    void createOrUpdateProfile_ShouldReturnOkWithJson() throws Exception {
        EmployerProfileDTO inputDto = new EmployerProfileDTO();
        inputDto.setCompanyName("New Tech");

        when(employerService.createOrUpdateProfile(eq(1L), any(EmployerProfileDTO.class)))
                .thenReturn(inputDto);

        mockMvc.perform(post("/employer/profile/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.companyName").value("New Tech"));
    }
}