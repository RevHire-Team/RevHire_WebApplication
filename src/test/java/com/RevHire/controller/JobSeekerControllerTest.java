package com.RevHire.controller;

import com.RevHire.dto.ApplicationResponseDTO;
import com.RevHire.dto.FavoriteJobDTO;
import com.RevHire.entity.*;
import com.RevHire.repository.*;
import com.RevHire.service.JobSeekerService;
import com.RevHire.service.ResumeService;
import com.RevHire.service.impl.ApplicationServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class JobSeekerControllerTest {

    private MockMvc mockMvc;

    @Mock
    private JobSeekerService jobSeekerService;

    @Mock
    private ResumeService resumeService;

    @Mock
    private ResumeRepository resumeRepo;

    @Mock
    private FavoriteJobRepository favoriteJobRepo;

    @Mock
    private JobSeekerProfileRepository profileRepo;

    @Mock
    private ResumeEducationRepository educationRepo;

    @Mock
    private ResumeExperienceRepository experienceRepo;

    @Mock
    private ApplicationServiceImpl applicationService;

    @InjectMocks
    private JobSeekerController jobSeekerController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {

        MockitoAnnotations.openMocks(this);

        mockMvc = MockMvcBuilders
                .standaloneSetup(jobSeekerController)
                .build();

        objectMapper = new ObjectMapper();
    }

    // =========================
    // CREATE PROFILE
    // =========================
    @Test
    void testCreateProfile() throws Exception {

        JobSeekerProfile profile = new JobSeekerProfile();

        when(jobSeekerService.createProfile(any(JobSeekerProfile.class), eq(1L)))
                .thenReturn(profile);

        mockMvc.perform(post("/api/jobseeker/profile/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(profile)))
                .andExpect(status().isOk());
    }

    // =========================
    // GET PROFILE
    // =========================
    @Test
    void testGetProfile() throws Exception {

        JobSeekerProfile profile = new JobSeekerProfile();
        profile.setSeekerId(1L);
        profile.setFullName("John Doe");

        when(jobSeekerService.getProfile(1L))
                .thenReturn(Optional.of(profile));

        when(resumeRepo.findBySeekerSeekerId(1L))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/jobseeker/profile/1"))
                .andExpect(status().isOk());
    }

    // =========================
    // UPDATE PROFILE
    // =========================
    @Test
    void testUpdateProfile() throws Exception {

        JobSeekerProfile profile = new JobSeekerProfile();
        profile.setFullName("Updated");

        when(profileRepo.findByUserUserId(1L))
                .thenReturn(Optional.of(new JobSeekerProfile()));

        when(profileRepo.save(any())).thenReturn(profile);

        mockMvc.perform(put("/api/jobseeker/profile/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(profile)))
                .andExpect(status().isOk());
    }

    // =========================
    // UPLOAD RESUME
    // =========================
    @Test
    void testUploadResume() throws Exception {

        ResumeFile file = new ResumeFile();
        file.setFileName("resume.pdf");

        MockMultipartFile multipartFile =
                new MockMultipartFile("file", "resume.pdf",
                        "application/pdf", "test".getBytes());

        when(jobSeekerService.uploadResumeFile(eq(1L), any()))
                .thenReturn(file);

        mockMvc.perform(multipart("/api/jobseeker/resume/upload/1")
                        .file(multipartFile))
                .andExpect(status().isOk());
    }

    // =========================
    // ADD FAVORITE JOB
    // =========================
    @Test
    void testAddFavorite() throws Exception {

        FavoriteJob mockFav = new FavoriteJob();
        mockFav.setFavId(1L);

        when(jobSeekerService.addFavoriteJob(1L, 2L))
                .thenReturn(mockFav);

        mockMvc.perform(post("/api/jobseeker/favorites/1/2"))
                .andExpect(status().isOk());
    }

    // =========================
    // GET FAVORITES
    // =========================
    @Test
    void testGetFavorites() throws Exception {

        List<FavoriteJobDTO> list = new ArrayList<>();

        when(jobSeekerService.getFavorites(1L)).thenReturn(list);

        mockMvc.perform(get("/api/jobseeker/favorites/1"))
                .andExpect(status().isOk());
    }

    // =========================
    // REMOVE FAVORITE
    // =========================
    @Test
    void testRemoveFavorite() throws Exception {

        doNothing().when(jobSeekerService).removeFavoriteJob(1L);

        mockMvc.perform(delete("/api/jobseeker/favorites/1"))
                .andExpect(status().isOk());
    }

    // =========================
    // MARK NOTIFICATION READ
    // =========================
    @Test
    void testMarkNotificationRead() throws Exception {

        doNothing().when(jobSeekerService).markNotificationAsRead(1L);

        mockMvc.perform(put("/api/jobseeker/notifications/1/read"))
                .andExpect(status().isOk());
    }

    // =========================
    // FAVORITE COUNT
    // =========================
    @Test
    void testFavoriteCount() throws Exception {

        JobSeekerProfile profile = new JobSeekerProfile();
        profile.setSeekerId(10L);

        when(profileRepo.findByUserUserId(1L))
                .thenReturn(Optional.of(profile));

        when(favoriteJobRepo.countBySeekerSeekerId(10L))
                .thenReturn(5L);

        mockMvc.perform(get("/api/jobseeker/api/jobseeker/favorites/count/1"))
                .andExpect(status().isOk());
    }

    // =========================
    // DASHBOARD
    // =========================
    @Test
    void testDashboard() throws Exception {

        JobSeekerProfile profile = new JobSeekerProfile();
        profile.setSeekerId(1L);

        when(profileRepo.findByUserUserId(1L))
                .thenReturn(Optional.of(profile));

        when(favoriteJobRepo.countBySeekerSeekerId(1L))
                .thenReturn(3L);

        when(applicationService.getApplicationsBySeeker(1L))
                .thenReturn(new ArrayList<ApplicationResponseDTO>());

        mockMvc.perform(get("/api/jobseeker/dashboard/1"))
                .andExpect(status().isOk());
    }

}