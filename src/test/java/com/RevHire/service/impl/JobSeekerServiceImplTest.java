package com.RevHire.service.impl;

import com.RevHire.dto.JobDTO;
import com.RevHire.entity.*;
import com.RevHire.repository.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class JobSeekerServiceImplTest {

    @Mock
    private JobSeekerProfileRepository profileRepo;

    @Mock
    private ResumeRepository resumeRepo;

    @Mock
    private ResumeFileRepository resumeFileRepo;

    @Mock
    private FavoriteJobRepository favoriteJobRepo;

    @Mock
    private NotificationRepository notificationRepo;

    @Mock
    private UserRepository userRepo;

    @Mock
    private JobRepository jobRepo;

    @Mock
    private ResumeSkillRepository resumeSkillRepo;

    @InjectMocks
    private JobSeekerServiceImpl jobSeekerService;

    private User user;
    private JobSeekerProfile profile;
    private Job job;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setUserId(1L);
        user.setEmail("seeker@test.com");

        profile = new JobSeekerProfile();
        profile.setSeekerId(1L);
        profile.setFullName("John Doe");
        profile.setUser(user);

        job = new Job();
        job.setJobId(1L);
        job.setTitle("Java Developer");
        job.setLocation("Bangalore");
        job.setSalaryMin(BigDecimal.valueOf(30000));
        job.setSalaryMax(BigDecimal.valueOf(50000));
        job.setJobType("FULL_TIME");
        job.setStatus("OPEN");
    }

    // ================= PROFILE TEST =================

    @Test
    void testCreateProfile() {

        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(profileRepo.save(profile)).thenReturn(profile);

        JobSeekerProfile result = jobSeekerService.createProfile(profile, 1L);

        assertNotNull(result);
        assertEquals("John Doe", result.getFullName());

        verify(profileRepo).save(profile);
    }

    @Test
    void testCreateProfileUserNotFound() {

        when(userRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> jobSeekerService.createProfile(profile, 1L));
    }

    // ================= UPDATE PROFILE =================

    @Test
    void testUpdateProfile() {

        when(profileRepo.findById(1L)).thenReturn(Optional.of(profile));
        when(profileRepo.save(any(JobSeekerProfile.class))).thenReturn(profile);

        JobSeekerProfile updated =
                jobSeekerService.updateProfile(1L, profile);

        assertNotNull(updated);
        verify(profileRepo).save(profile);
    }

    // ================= RESUME =================

    @Test
    void testGetOrCreateResumeExisting() {

        Resume resume = new Resume();
        resume.setSeeker(profile);

        when(profileRepo.findByUserUserId(1L))
                .thenReturn(Optional.of(profile));

        when(resumeRepo.findBySeekerSeekerId(1L))
                .thenReturn(Optional.of(resume));

        Resume result = jobSeekerService.getOrCreateResume(1L);

        assertNotNull(result);
    }

    @Test
    void testGetOrCreateResumeNew() {

        when(profileRepo.findByUserUserId(1L))
                .thenReturn(Optional.of(profile));

        when(resumeRepo.findBySeekerSeekerId(1L))
                .thenReturn(Optional.empty());

        when(resumeRepo.save(any(Resume.class)))
                .thenReturn(new Resume());

        Resume result = jobSeekerService.getOrCreateResume(1L);

        assertNotNull(result);
        verify(resumeRepo).save(any(Resume.class));
    }

    // ================= FAVORITE JOB =================

    @Test
    void testAddFavoriteJob() {

        when(profileRepo.findById(1L)).thenReturn(Optional.of(profile));
        when(jobRepo.findById(1L)).thenReturn(Optional.of(job));
        when(favoriteJobRepo.existsBySeekerSeekerIdAndJobJobId(1L,1L))
                .thenReturn(false);

        FavoriteJob fav = new FavoriteJob();
        when(favoriteJobRepo.save(any(FavoriteJob.class)))
                .thenReturn(fav);

        FavoriteJob result =
                jobSeekerService.addFavoriteJob(1L,1L);

        assertNotNull(result);
        verify(favoriteJobRepo).save(any(FavoriteJob.class));
    }

    @Test
    void testAddFavoriteJobAlreadyExists() {

        when(profileRepo.findById(1L)).thenReturn(Optional.of(profile));
        when(jobRepo.findById(1L)).thenReturn(Optional.of(job));

        when(favoriteJobRepo
                .existsBySeekerSeekerIdAndJobJobId(1L,1L))
                .thenReturn(true);

        assertThrows(RuntimeException.class,
                () -> jobSeekerService.addFavoriteJob(1L,1L));
    }

    // ================= NOTIFICATION =================

    @Test
    void testMarkNotificationAsRead() {

        Notification notification = new Notification();
        notification.setNotificationId(1L);
        notification.setIsRead(false);

        when(notificationRepo.findById(1L))
                .thenReturn(Optional.of(notification));

        jobSeekerService.markNotificationAsRead(1L);

        assertTrue(notification.getIsRead());
        verify(notificationRepo).save(notification);
    }

    // ================= JOB SEARCH =================

    @Test
    void testSearchJobs() {

        when(jobRepo.findAdvanced(
                anyString(),
                anyString(),
                anyInt(),
                anyString(),
                any(BigDecimal.class),
                any(BigDecimal.class),
                anyString(),
                eq("OPEN")
        )).thenReturn(List.of(job));

        List<JobDTO> result =
                jobSeekerService.searchJobs(
                        "Java",
                        "Bangalore",
                        2,
                        "BTech",
                        20000.0,
                        60000.0,
                        "FULL_TIME"
                );

        assertEquals(1, result.size());
        assertEquals("Java Developer", result.get(0).getTitle());
    }

}