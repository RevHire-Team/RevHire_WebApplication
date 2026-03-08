package com.RevHire.service.impl;

import com.RevHire.entity.*;
import com.RevHire.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ApplicationServiceImplTest {

    @InjectMocks
    private ApplicationServiceImpl applicationService;

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private ApplicationNoteRepository applicationNoteRepository;

    @Mock
    private JobRepository jobRepository;

    @Mock
    private JobSeekerProfileRepository seekerRepository;

    @Mock
    private ResumeRepository resumeRepository;

    @Mock
    private EmployerProfileRepository employerRepository;

    private Job job;
    private JobSeekerProfile seeker;
    private Resume resume;
    private Application application;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        job = new Job();
        job.setJobId(1L);
        job.setTitle("Java Developer");

        seeker = new JobSeekerProfile();
        seeker.setSeekerId(1L);
        seeker.setFullName("John Doe");

        resume = new Resume();
        resume.setResumeId(1L);

        application = new Application();
        application.setApplicationId(1L);
        application.setJob(job);
        application.setSeeker(seeker);
        application.setResume(resume);
        application.setStatus("APPLIED");
    }

    @Test
    void testApplyJobSuccess() {

        when(applicationRepository
                .findByJobJobIdAndSeekerSeekerId(1L,1L))
                .thenReturn(Optional.empty());

        when(jobRepository.findById(1L))
                .thenReturn(Optional.of(job));

        when(seekerRepository.findById(1L))
                .thenReturn(Optional.of(seeker));

        when(resumeRepository.findById(1L))
                .thenReturn(Optional.of(resume));

        when(applicationRepository.save(any(Application.class)))
                .thenReturn(application);

        Application result = applicationService.applyJob(
                1L,
                1L,
                1L,
                "I am interested in this job"
        );

        assertNotNull(result);
        assertEquals("APPLIED", result.getStatus());
    }

    @Test
    void testApplyJobAlreadyApplied() {

        when(applicationRepository
                .findByJobJobIdAndSeekerSeekerId(1L,1L))
                .thenReturn(Optional.of(application));

        assertThrows(RuntimeException.class, () ->
                applicationService.applyJob(1L,1L,1L,"test"));
    }

    @Test
    void testWithdrawApplication() {

        when(applicationRepository.findById(1L))
                .thenReturn(Optional.of(application));

        applicationService.withdrawApplication(1L,"Changed mind");

        assertEquals("WITHDRAWN", application.getStatus());

        verify(applicationRepository).save(application);
    }

    @Test
    void testUpdateStatus() {

        when(applicationRepository.findById(1L))
                .thenReturn(Optional.of(application));

        when(applicationRepository.save(any(Application.class)))
                .thenReturn(application);

        Application updated =
                applicationService.updateStatus(1L,"SHORTLISTED");

        assertEquals("SHORTLISTED", updated.getStatus());
    }

    @Test
    void testUpdateStatusInvalid() {

        when(applicationRepository.findById(1L))
                .thenReturn(Optional.of(application));

        assertThrows(RuntimeException.class,
                () -> applicationService.updateStatus(1L,"INVALID_STATUS"));
    }

    @Test
    void testAddEmployerNotes() {

        EmployerProfile employer = new EmployerProfile();
        employer.setEmployerId(1L);

        when(applicationRepository.findById(1L))
                .thenReturn(Optional.of(application));

        when(employerRepository.findById(1L))
                .thenReturn(Optional.of(employer));

        String result = applicationService.addEmployerNotes(
                1L,
                1L,
                "Candidate looks promising"
        );

        assertEquals("Note added successfully", result);

        verify(applicationNoteRepository).save(any(ApplicationNote.class));
    }
}