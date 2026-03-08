package com.RevHire.controller;

import com.RevHire.dto.ApplicationResponseDTO;
import com.RevHire.dto.FavoriteJobDTO;
import com.RevHire.dto.JobDTO;
import com.RevHire.entity.JobSeekerProfile;
import com.RevHire.entity.User;
import com.RevHire.repository.JobSeekerProfileRepository;
import com.RevHire.repository.ResumeRepository;
import com.RevHire.repository.ResumeSkillRepository;
import com.RevHire.repository.UserRepository;
import com.RevHire.service.ApplicationService;
import com.RevHire.service.JobSeekerService;
import com.RevHire.service.ResumeService;
import com.RevHire.service.impl.JobServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(JobSeekerUIController.class)
class JobSeekerUIControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JobSeekerService jobSeekerService;

    @MockBean
    private ResumeService resumeService;

    @MockBean
    private ResumeRepository resumeRepo;

    @MockBean
    private JobServiceImpl jobService;

    @MockBean
    private ApplicationService applicationService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private ResumeSkillRepository resumeSkillRepo;

    @MockBean
    private JobSeekerProfileRepository profileRepo;

    private User user;
    private JobSeekerProfile profile;

    @BeforeEach
    void setUp() {

        user = new User();
        user.setUserId(1L);
        user.setEmail("john@test.com");

        profile = new JobSeekerProfile();
        profile.setSeekerId(10L);
        profile.setFullName("John Doe");
        profile.setProfileCompletion(80);
    }

    // ================= DASHBOARD =================

    @Test
    void testDashboard() throws Exception {

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(jobSeekerService.getProfile(1L)).thenReturn(Optional.of(profile));

        mockMvc.perform(get("/jobseeker/dashboard/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("jobseeker/dashboard"))
                .andExpect(model().attributeExists("userId"))
                .andExpect(model().attributeExists("userName"));
    }

    // ================= PROFILE PAGE =================

    @Test
    void testManageProfile() throws Exception {

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("userId", 1L);

        mockMvc.perform(get("/jobseeker/profile/manage").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("jobseeker/profile"));
    }

    // ================= EDIT PROFILE =================

    @Test
    void testEditProfile() throws Exception {

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("userId", 1L);

        mockMvc.perform(get("/jobseeker/edit-profile").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("jobseeker/edit-profile"));
    }

    // ================= RESUME BUILDER =================

    @Test
    void testResumeBuilder() throws Exception {

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("userId", 1L);

        mockMvc.perform(get("/jobseeker/resume/builder").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("jobseeker/resume-builder"));
    }

    // ================= JOB SEARCH =================

    @Test
    void testSearchJobs() throws Exception {

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("userId", 1L);

        JobDTO job = new JobDTO(
                1L,
                "Java Developer",
                "ABC Company",
                new BigDecimal("50000"),
                new BigDecimal("80000"),
                "Bangalore",
                "FULL_TIME",
                "OPEN"
        );

        when(jobService.searchJobs(
                any(), any(), any(), any(), any(), any(), any()
        )).thenReturn(List.of(job));

        mockMvc.perform(get("/jobseeker/jobs/search").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("jobseeker/search-jobs"))
                .andExpect(model().attributeExists("jobs"));
    }

    // ================= APPLICATIONS =================

    @Test
    void testApplicationsPage() throws Exception {

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("userId", 1L);

        ApplicationResponseDTO dto = new ApplicationResponseDTO(
                1L,
                "Java Developer",
                "John Doe",
                "john@test.com",
                "APPLIED",
                LocalDateTime.now()
        );

        when(jobSeekerService.getProfile(1L)).thenReturn(Optional.of(profile));
        when(applicationService.getApplicationsBySeeker(10L)).thenReturn(List.of(dto));

        mockMvc.perform(get("/jobseeker/applications").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("jobseeker/applications"))
                .andExpect(model().attributeExists("applications"));
    }

    // ================= SAVED JOBS =================

    @Test
    void testSavedJobs() throws Exception {

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("userId", 1L);

        FavoriteJobDTO favoriteJob = new FavoriteJobDTO(
                1L,
                1L,
                "Java Developer",
                "ABC Company",
                new BigDecimal("50000"),
                new BigDecimal("80000"),
                "Bangalore",
                "FULL_TIME",
                "OPEN"
        );

        when(jobSeekerService.getProfile(1L)).thenReturn(Optional.of(profile));
        when(jobSeekerService.getFavorites(10L)).thenReturn(List.of(favoriteJob));

        mockMvc.perform(get("/jobseeker/jobs/saved").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("jobseeker/saved-jobs"))
                .andExpect(model().attributeExists("savedJobs"));
    }

}