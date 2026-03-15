package com.RevHire.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import com.RevHire.controller.ApplicationController;
import com.RevHire.dto.EmployerApplicationDTO;
import com.RevHire.entity.*;
import com.RevHire.repository.*;
import com.RevHire.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

@ExtendWith(MockitoExtension.class)
public class ApplicationServiceImplTest {

    @Mock private ApplicationRepository applicationRepository;
    @Mock private ApplicationNoteRepository applicationNoteRepository;
    @Mock private JobRepository jobRepository;
    @Mock private JobSeekerProfileRepository seekerRepository;
    @Mock private ResumeRepository resumeRepository;
    @Mock private NotificationService notificationService;
    @Mock private EmployerProfileRepository employerRepository;

    @InjectMocks
    private ApplicationServiceImpl applicationService;

    private Job job;
    private JobSeekerProfile seeker;
    private Resume resume;
    private User user;
    private EmployerProfile employer;
    private Application application;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUserId(1L);
        user.setEmail("test@revhire.com");

        employer = new EmployerProfile();
        employer.setEmployerId(10L);
        employer.setUser(user);

        job = new Job();
        job.setJobId(100L);
        job.setTitle("Software Engineer");
        job.setEmployer(employer);

        seeker = new JobSeekerProfile();
        seeker.setSeekerId(50L);
        seeker.setFullName("John Doe");
        seeker.setUser(user);

        resume = new Resume();
        resume.setResumeId(200L);

        application = new Application();
        application.setApplicationId(1000L);
        application.setJob(job);
        application.setSeeker(seeker);
        application.setResume(resume);
        application.setStatus("APPLIED");
    }

    // --- applyJob Tests ---

    @Test
    void applyJob_Success() {
        when(applicationRepository.findByJobJobIdAndSeekerSeekerId(100L, 50L)).thenReturn(Optional.empty());
        when(jobRepository.findById(100L)).thenReturn(Optional.of(job));
        when(seekerRepository.findById(50L)).thenReturn(Optional.of(seeker));
        when(resumeRepository.findById(200L)).thenReturn(Optional.of(resume));
        when(applicationRepository.save(any(Application.class))).thenReturn(application);

        Application result = applicationService.applyJob(100L, 50L, 200L, "Cover Letter");

        assertNotNull(result);
        verify(notificationService, times(1)).sendNotification(eq(1L), anyString());
        verify(applicationRepository).save(any(Application.class));
    }

    @Test
    void applyJob_AlreadyApplied_ThrowsException() {
        when(applicationRepository.findByJobJobIdAndSeekerSeekerId(100L, 50L)).thenReturn(Optional.of(application));

        assertThrows(RuntimeException.class, () ->
                applicationService.applyJob(100L, 50L, 200L, "Cover Letter")
        );
    }

    // --- updateStatus Tests ---

    @Test
    void updateStatus_Shortlisted_SendsSpecificNotification() {
        when(applicationRepository.findById(1000L)).thenReturn(Optional.of(application));
        when(applicationRepository.save(any(Application.class))).thenReturn(application);

        applicationService.updateStatus(1000L, "SHORTLISTED");

        assertEquals("SHORTLISTED", application.getStatus());
        verify(notificationService).sendNotification(eq(1L), contains("SHORTLISTED"));
    }

    @Test
    void updateStatus_InvalidStatus_ThrowsException() {
        when(applicationRepository.findById(1000L)).thenReturn(Optional.of(application));

        assertThrows(RuntimeException.class, () ->
                applicationService.updateStatus(1000L, "INVALID_STATUS")
        );
    }

    // --- withdrawApplication Tests ---

    @Test
    void withdrawApplication_Success() {
        when(applicationRepository.findById(1000L)).thenReturn(Optional.of(application));

        applicationService.withdrawApplication(1000L, "Found another job");

        assertEquals("WITHDRAWN", application.getStatus());
        assertEquals("Found another job", application.getWithdrawReason());
        verify(applicationRepository).save(application);
    }

    // --- List Retrieval Tests ---

    @Test
    void getApplicationsByEmployer_Success() {
        when(employerRepository.findByUserUserId(1L)).thenReturn(Optional.of(employer));
        when(applicationRepository.findByJobEmployerEmployerId(10L)).thenReturn(List.of(application));

        List<EmployerApplicationDTO> results = applicationService.getApplicationsByEmployer(1L);

        assertFalse(results.isEmpty());
        assertEquals("Software Engineer", results.get(0).getJobTitle());
    }

    // --- addEmployerNotes Tests ---

    @Test
    void addEmployerNotes_Success() {
        when(applicationRepository.findById(1000L)).thenReturn(Optional.of(application));
        when(employerRepository.findById(10L)).thenReturn(Optional.of(employer));

        String response = applicationService.addEmployerNotes(1000L, 10L, "Great candidate");

        assertEquals("Note added successfully", response);
        verify(applicationNoteRepository, times(1)).save(any(ApplicationNote.class));
    }
}