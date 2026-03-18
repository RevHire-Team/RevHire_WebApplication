package com.RevHire.controller;

import com.RevHire.dto.ApplicationResponseDTO;
import com.RevHire.dto.EmployerApplicationDTO;
import com.RevHire.dto.NoteRequestDTO;
import com.RevHire.entity.User;
import com.RevHire.service.ApplicationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ApplicationController.class)
class ApplicationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ApplicationService applicationService;

    @Autowired
    private ObjectMapper objectMapper;

    private MockHttpSession session;
    private User mockUser;

    @BeforeEach
    void setUp() {
        session = new MockHttpSession();
        mockUser = new User();
        mockUser.setUserId(1L);
    }

    // ================= UI / VIEW TESTS =================

    @Test
    void testShowApplyPage_Authorized() throws Exception {
        session.setAttribute("userId", 1L);

        mockMvc.perform(get("/applications/jobseeker/jobs/apply/10").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("jobseeker/applications"))
                .andExpect(model().attributeExists("jobId"))
                .andExpect(model().attribute("userId", 1L));
    }

    @Test
    void testShowApplyPage_Unauthorized() throws Exception {
        // No session attribute set
        mockMvc.perform(get("/applications/jobseeker/jobs/apply/10").session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login"));
    }

    // ================= POST / REST TESTS =================

    @Test
    void testApply_Success() throws Exception {
        mockMvc.perform(post("/applications/submit-application")
                        .param("jobId", "10")
                        .param("userId", "1")
                        .param("resumeId", "5")
                        .param("coverLetter", "Hire me!"))
                .andExpect(status().isOk())
                .andExpect(content().string("Application submitted successfully"));
    }

    @Test
    void testApply_AlreadyAppliedConflict() throws Exception {
        // Use any() for all slots to ensure the mock matches regardless of nulls
        doThrow(new RuntimeException("Already applied to this job"))
                .when(applicationService)
                .applyJob(any(), any(), any(), any(), any());

        mockMvc.perform(post("/applications/submit-application")
                        .param("jobId", "10")
                        .param("userId", "1")
                        .param("resumeId", "5"))
                // Now this should correctly hit the 'catch' block in your controller
                .andExpect(status().isConflict())
                .andExpect(content().string("Already applied to this job"));
    }

    @Test
    void testUpdateStatus_Success() throws Exception {
        mockMvc.perform(post("/applications/update-status/1")
                        .param("status", "ACCEPTED"))
                .andExpect(status().isOk())
                .andExpect(content().string("Application status updated successfully"));
    }

    @Test
    void testAddNotes_Success() throws Exception {
        NoteRequestDTO request = new NoteRequestDTO();
        request.setEmployerId(1L);
        request.setNoteText("Great candidate");

        mockMvc.perform(put("/applications/notes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    // ================= DATA FETCHING TESTS =================

    @Test
    void testGetAllApplications_Unauthorized() throws Exception {
        mockMvc.perform(get("/applications/all").session(session))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetAllApplications_Authorized() throws Exception {
        session.setAttribute("loggedInUser", mockUser);
        EmployerApplicationDTO dto = new EmployerApplicationDTO();
        dto.setApplicationId(1L);

        when(applicationService.getApplicationsByEmployer(1L))
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/applications/all").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].applicationId").value(1));
    }

}