package com.RevHire.service.impl;

import com.RevHire.dto.ApplicationResponseDTO;
import com.RevHire.dto.EmployerApplicationDTO;
import com.RevHire.entity.*;
import com.RevHire.repository.*;
import com.RevHire.service.NotificationService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApplicationServiceImplTest {

    @InjectMocks
    private ApplicationServiceImpl applicationService;

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private JobRepository jobRepository;

    @Mock
    private JobSeekerProfileRepository seekerRepository;

    @Mock
    private ResumeRepository resumeRepository;

    @Mock
    private ResumeFileRepository resumeFileRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private EmployerProfileRepository employerRepository;

    @Mock
    private ApplicationNoteRepository applicationNoteRepository;

    private Job job;
    private JobSeekerProfile seeker;
    private Resume resume;
    private ResumeFile resumeFile;
    private Application application;

    @BeforeEach
    void setup() {
        // Job and employer
        job = new Job();
        job.setJobId(100L);
        EmployerProfile employer = new EmployerProfile();
        employer.setEmployerId(10L);
        User employerUser = new User();
        employerUser.setUserId(5L);
        employer.setUser(employerUser);
        job.setEmployer(employer);

        // Seeker
        seeker = new JobSeekerProfile();
        seeker.setSeekerId(1L);
        User seekerUser = new User();
        seekerUser.setUserId(1L);
        seeker.setUser(seekerUser);
        seeker.setFullName("John Doe");

        // Resume
        resume = new Resume();
        resume.setResumeId(200L);
        resume.setSeeker(seeker);

        // Resume File
        resumeFile = new ResumeFile();
        resumeFile.setFileId(300L);
        resumeFile.setResume(resume);

        // Application
        application = new Application();
        application.setApplicationId(1000L);
        application.setJob(job);
        application.setSeeker(seeker);
        application.setResume(resume);
        application.setStatus("APPLIED");
        application.setAppliedDate(LocalDateTime.now());
    }

    // ===================== APPLY JOB =====================
    @Test
    void applyJob_ShouldSaveApplication_WhenValidWithFile() {
        // Stubbing
        lenient().when(applicationRepository.findByJobJobIdAndSeekerSeekerId(anyLong(), anyLong()))
                .thenReturn(Optional.empty());
        when(jobRepository.findById(job.getJobId())).thenReturn(Optional.of(job));
        when(seekerRepository.findByUserUserId(seeker.getUser().getUserId())).thenReturn(Optional.of(seeker));
        when(resumeRepository.findBySeeker_SeekerId(seeker.getSeekerId())).thenReturn(Optional.of(resume));
        when(resumeFileRepository.findById(resumeFile.getFileId())).thenReturn(Optional.of(resumeFile));
        when(applicationRepository.save(any(Application.class))).thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(notificationService).sendNotification(anyLong(), anyString());

        // Execute
        Application result = applicationService.applyJob(
                job.getJobId(),
                seeker.getUser().getUserId(),
                resume.getResumeId(),
                resumeFile.getFileId(),
                "Cover Letter"
        );

        // Verify
        assertNotNull(result);
        assertEquals(job.getJobId(), result.getJob().getJobId());
        assertEquals(seeker.getSeekerId(), result.getSeeker().getSeekerId());
        assertEquals("APPLIED", result.getStatus());
        verify(notificationService, times(1)).sendNotification(anyLong(), anyString());
    }

    @Test
    void applyJob_ShouldSaveApplication_WhenValidWithoutFile() {
        // Stubbing
        lenient().when(applicationRepository.findByJobJobIdAndSeekerSeekerId(anyLong(), anyLong()))
                .thenReturn(Optional.empty());
        when(jobRepository.findById(job.getJobId())).thenReturn(Optional.of(job));
        when(seekerRepository.findByUserUserId(seeker.getUser().getUserId())).thenReturn(Optional.of(seeker));
        when(resumeRepository.findBySeeker_SeekerId(seeker.getSeekerId())).thenReturn(Optional.of(resume));
        when(applicationRepository.save(any(Application.class))).thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(notificationService).sendNotification(anyLong(), anyString());

        // Execute
        Application result = applicationService.applyJob(
                job.getJobId(),
                seeker.getUser().getUserId(),
                resume.getResumeId(),
                null,
                "Cover Letter"
        );

        // Verify
        assertNotNull(result);
        assertEquals(job.getJobId(), result.getJob().getJobId());
        assertEquals(seeker.getSeekerId(), result.getSeeker().getSeekerId());
        assertEquals("APPLIED", result.getStatus());
        verify(notificationService, times(1)).sendNotification(anyLong(), anyString());
    }

    // ===================== WITHDRAW APPLICATION =====================
    @Test
    void withdrawApplication_ShouldUpdateStatusToWithdrawn() {
        when(applicationRepository.findById(application.getApplicationId())).thenReturn(Optional.of(application));
        when(applicationRepository.save(any(Application.class))).thenAnswer(invocation -> invocation.getArgument(0));

        applicationService.withdrawApplication(application.getApplicationId(), "No longer interested");

        assertEquals("WITHDRAWN", application.getStatus());
        assertEquals("No longer interested", application.getWithdrawReason());
    }

    // ===================== UPDATE STATUS =====================
    @Test
    void updateStatus_ShouldUpdateApplicationStatusAndNotify() {
        when(applicationRepository.findById(application.getApplicationId())).thenReturn(Optional.of(application));
        when(applicationRepository.save(any(Application.class))).thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(notificationService).sendNotification(anyLong(), anyString());

        Application updated = applicationService.updateStatus(application.getApplicationId(), "SHORTLISTED");

        assertEquals("SHORTLISTED", updated.getStatus());
        verify(notificationService, times(1)).sendNotification(anyLong(), anyString());
    }

    @Test
    void updateStatus_ShouldThrow_WhenInvalidStatus() {
        when(applicationRepository.findById(application.getApplicationId())).thenReturn(Optional.of(application));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> applicationService.updateStatus(application.getApplicationId(), "INVALID_STATUS"));

        assertEquals("Invalid status value", ex.getMessage());
    }

    // ===================== GET APPLICATIONS =====================
    @Test
    void getApplicationsByJob_ShouldReturnApplications() {
        when(applicationRepository.findByJobJobId(job.getJobId())).thenReturn(List.of(application));

        List<ApplicationResponseDTO> results = applicationService.getApplicationsByJob(job.getJobId());

        assertEquals(1, results.size());
        assertEquals(application.getApplicationId(), results.get(0).getId());
    }

    @Test
    void getApplicationsBySeeker_ShouldReturnApplications() {
        when(applicationRepository.findBySeekerSeekerId(seeker.getSeekerId())).thenReturn(List.of(application));

        List<ApplicationResponseDTO> results = applicationService.getApplicationsBySeeker(seeker.getSeekerId());

        assertEquals(1, results.size());
        assertEquals(application.getApplicationId(), results.get(0).getId());
    }

    @Test
    void getApplicationsByEmployer_ShouldReturnApplications() {
        when(employerRepository.findByUserUserId(seeker.getUser().getUserId()))
                .thenReturn(Optional.of(job.getEmployer()));
        when(applicationRepository.findByJobEmployerEmployerId(job.getEmployer().getEmployerId()))
                .thenReturn(List.of(application));

        List<EmployerApplicationDTO> results = applicationService.getApplicationsByEmployer(seeker.getUser().getUserId());

        assertEquals(1, results.size());
        assertEquals(application.getApplicationId(), results.get(0).getApplicationId());
    }

    // ===================== ADD EMPLOYER NOTES =====================
    @Test
    void addEmployerNotes_ShouldSaveNote() {
        when(applicationRepository.findById(application.getApplicationId())).thenReturn(Optional.of(application));
        when(employerRepository.findById(job.getEmployer().getEmployerId())).thenReturn(Optional.of(job.getEmployer()));
        when(applicationNoteRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        String response = applicationService.addEmployerNotes(application.getApplicationId(),
                job.getEmployer().getEmployerId(),
                "Great candidate");

        assertEquals("Note added successfully", response);
    }
}