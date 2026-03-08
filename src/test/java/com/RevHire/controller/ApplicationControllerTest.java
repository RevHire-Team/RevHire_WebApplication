package com.RevHire.controller;

import com.RevHire.dto.ApplicationResponseDTO;
import com.RevHire.dto.EmployerApplicationDTO;
import com.RevHire.dto.NoteRequestDTO;
import com.RevHire.entity.User;
import com.RevHire.service.ApplicationService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
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

    // ---------- showApplyPage ----------

    @Test
    void showApplyPage_ShouldReturnApplyPage_WhenUserLoggedIn() throws Exception {

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("userId", 1L);

        mockMvc.perform(get("/applications/jobseeker/jobs/apply/10")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("jobseeker/applications"))
                .andExpect(model().attributeExists("jobId"))
                .andExpect(model().attributeExists("userId"));
    }

    @Test
    void showApplyPage_ShouldRedirectToLogin_WhenUserNotLoggedIn() throws Exception {

        mockMvc.perform(get("/applications/jobseeker/jobs/apply/10"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login"));
    }

    // ---------- apply ----------

    @Test
    void apply_ShouldReturnSuccessMessage() throws Exception {

        doNothing().when(applicationService)
                .applyJob(1L, 2L, 3L, "Cover letter");

        mockMvc.perform(post("/applications/submit-application")
                        .param("jobId", "1")
                        .param("seekerId", "2")
                        .param("resumeId", "3")
                        .param("coverLetter", "Cover letter"))
                .andExpect(status().isOk())
                .andExpect(content().string("Application submitted successfully"));
    }

    // ---------- getBySeeker ----------

    @Test
    void getBySeeker_ShouldReturnApplications() throws Exception {

        List<ApplicationResponseDTO> list = List.of(
                new ApplicationResponseDTO(
                        1L,
                        "John Doe",
                        "john@example.com",
                        "Software Engineer",
                        "APPLIED",
                        LocalDateTime.now()
                )
        );

        when(applicationService.getApplicationsBySeeker(2L)).thenReturn(list);

        mockMvc.perform(get("/applications/seeker/2"))
                .andExpect(status().isOk());
    }

    // ---------- getByJob ----------

    @Test
    void getByJob_ShouldReturnApplications() throws Exception {

        List<ApplicationResponseDTO> list = List.of(
                new ApplicationResponseDTO(
                        1L,
                        "John Doe",
                        "john@example.com",
                        "Software Engineer",
                        "APPLIED",
                        LocalDateTime.now()
                )
        );

        when(applicationService.getApplicationsByJob(10L)).thenReturn(list);

        mockMvc.perform(get("/applications/job/10"))
                .andExpect(status().isOk());
    }

    // ---------- getByEmployer ----------

    @Test
    void getByEmployer_ShouldReturnEmployerApplications() throws Exception {

        List<EmployerApplicationDTO> list = List.of(
                new EmployerApplicationDTO(
                        1L,
                        "Software Engineer",
                        100L,
                        "John Doe",
                        "john@example.com",
                        "APPLIED",
                        LocalDateTime.now()
                )
        );

        when(applicationService.getApplicationsByEmployer(1L)).thenReturn(list);

        mockMvc.perform(get("/applications/employer/1"))
                .andExpect(status().isOk());
    }

    // ---------- withdraw ----------

    @Test
    void withdraw_ShouldReturnSuccessMessage() throws Exception {

        doNothing().when(applicationService)
                .withdrawApplication(1L, "Not interested");

        mockMvc.perform(post("/applications/withdraw/1")
                        .param("reason", "Not interested"))
                .andExpect(status().isOk())
                .andExpect(content().string("Application withdrawn successfully"));
    }

    // ---------- updateStatus ----------

    @Test
    void updateStatus_ShouldReturnSuccessMessage() throws Exception {

        doNothing().when(applicationService)
                .updateStatus(1L, "APPROVED");

        mockMvc.perform(post("/applications/update-status/1")
                        .param("status", "APPROVED"))
                .andExpect(status().isOk())
                .andExpect(content().string("Application status updated successfully"));
    }

    // ---------- addNotes ----------

    @Test
    void addNotes_ShouldReturnSuccess() throws Exception {

        NoteRequestDTO request = new NoteRequestDTO();
        request.setEmployerId(1L);
        request.setNoteText("Good candidate");

        when(applicationService.addEmployerNotes(1L, 1L, "Good candidate"))
                .thenReturn("Note added");

        mockMvc.perform(put("/applications/notes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    // ---------- showManageApplicationsPage ----------

    @Test
    void showManageApplicationsPage_ShouldReturnPage_WhenLoggedIn() throws Exception {

        MockHttpSession session = new MockHttpSession();

        User user = new User();
        user.setUserId(1L);

        session.setAttribute("loggedInUser", user);

        mockMvc.perform(get("/applications/manage")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("employer/applications/manage-applications"));
    }

    @Test
    void showManageApplicationsPage_ShouldRedirect_WhenNotLoggedIn() throws Exception {

        mockMvc.perform(get("/applications/manage"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login"));
    }

    // ---------- getAllApplications ----------

    @Test
    void getAllApplications_ShouldReturnApplications() throws Exception {

        User user = new User();
        user.setUserId(1L);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("loggedInUser", user);

        List<EmployerApplicationDTO> list = List.of(
                new EmployerApplicationDTO(
                        1L,
                        "Software Engineer",
                        100L,
                        "John Doe",
                        "john@example.com",
                        "APPLIED",
                        LocalDateTime.now()
                )
        );

        when(applicationService.getApplicationsByEmployer(1L)).thenReturn(list);

        mockMvc.perform(get("/applications/all")
                        .session(session))
                .andExpect(status().isOk());
    }

    @Test
    void getAllApplications_ShouldReturn401_WhenNotLoggedIn() throws Exception {

        mockMvc.perform(get("/applications/all"))
                .andExpect(status().isUnauthorized());
    }
}