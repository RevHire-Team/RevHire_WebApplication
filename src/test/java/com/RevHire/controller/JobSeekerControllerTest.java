package com.RevHire.controller;

import com.RevHire.dto.ApplicationResponseDTO;
import com.RevHire.entity.*;
import com.RevHire.repository.*;
import com.RevHire.service.JobSeekerService;
import com.RevHire.service.impl.ApplicationServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(JobSeekerController.class)
class JobSeekerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // Mocking all constructor-injected dependencies
    @MockBean private JobSeekerService jobSeekerService;
    @MockBean private com.RevHire.service.ResumeService resumeService;
    @MockBean private ApplicationServiceImpl applicationService;
    @MockBean private ResumeRepository resumeRepo;
    @MockBean private FavoriteJobRepository favoriteJobRepo;
    @MockBean private JobSeekerProfileRepository profileRepo;
    @MockBean private ResumeEducationRepository educationRepo;
    @MockBean private ResumeExperienceRepository experienceRepo;
    @MockBean private ResumeCertificationRepository certificationRepo;
    @MockBean private ResumeProjectRepository projectRepo;
    @MockBean private ResumeSkillRepository resumeSkillRepo;

    private JobSeekerProfile mockProfile;

    @BeforeEach
    void setUp() {
        mockProfile = new JobSeekerProfile();
        mockProfile.setSeekerId(1L);
        mockProfile.setFullName("John Doe");
        mockProfile.setProfileCompletion(80);
    }

    // ---------- Profile Tests ----------

    @Test
    void getProfile_ShouldReturnFormattedMap() throws Exception {
        when(jobSeekerService.getProfile(1L)).thenReturn(Optional.of(mockProfile));
        when(resumeRepo.findTopBySeekerSeekerIdOrderByResumeIdDesc(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/jobseeker/profile/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("John Doe"));
    }

    @Test
    void updateProfile_ShouldReturnSuccess() throws Exception {
        when(profileRepo.findByUserUserId(anyLong())).thenReturn(Optional.of(mockProfile));
        when(resumeRepo.findTopBySeekerSeekerIdOrderByResumeIdDesc(anyLong())).thenReturn(Optional.of(new Resume()));

        Map<String, Object> updateData = Map.of(
                "fullName", "John Updated",
                "phone", "1234567890",
                "location", "New York",
                "experience", "5 years of Java"
        );

        mockMvc.perform(put("/api/jobseeker/profile/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Profile updated successfully"));
    }

    // ---------- Resume Upload Test ----------

    @Test
    void uploadResume_ShouldReturnFileName_WhenSuccessful() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "resume.pdf",
                MediaType.APPLICATION_PDF_VALUE, "test content".getBytes());

        ResumeFile mockFile = new ResumeFile();
        mockFile.setFileName("resume.pdf");

        when(jobSeekerService.uploadResumeFile(eq(1L), any())).thenReturn(mockFile);

        mockMvc.perform(multipart("/api/jobseeker/resume/upload/1").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Resume uploaded successfully"))
                .andExpect(jsonPath("$.fileName").value("resume.pdf"));
    }

    // ---------- Dashboard Test ----------

    @Test
    void getDashboard_ShouldReturnStats() throws Exception {
        when(profileRepo.findByUserUserId(1L)).thenReturn(Optional.of(mockProfile));
        when(favoriteJobRepo.countBySeekerSeekerId(anyLong())).thenReturn(5L);
        when(applicationService.getApplicationsBySeeker(anyLong()))
                .thenReturn(Collections.singletonList(new ApplicationResponseDTO()));

        mockMvc.perform(get("/api/jobseeker/dashboard/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.profileScore").value(80))
                .andExpect(jsonPath("$.savedJobs").value(5))
                .andExpect(jsonPath("$.totalApplications").value(1));
    }

    // ---------- Favorite Jobs Test ----------

    @Test
    void addFavorite_ShouldReturnConflict_WhenAlreadySaved() throws Exception {
        // Simulating the RuntimeException thrown by service when job is already saved
        when(jobSeekerService.addFavoriteJob(1L, 101L))
                .thenThrow(new RuntimeException("Already saved"));

        mockMvc.perform(post("/api/jobseeker/favorites/1/101"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value("error"));
    }
}