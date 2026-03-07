package com.RevHire.service;

import com.RevHire.dto.ApplicationResponseDTO;
import com.RevHire.dto.EmployerApplicationDTO;
import com.RevHire.entity.*;
import com.RevHire.repository.*;
import com.RevHire.service.impl.ApplicationServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ApplicationServiceTest {

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

    @InjectMocks
    private ApplicationServiceImpl applicationService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    // -------- APPLY JOB --------

    @Test
    void testApplyJob() {

        Job job = new Job();
        job.setJobId(1L);

        JobSeekerProfile seeker = new JobSeekerProfile();
        seeker.setSeekerId(2L);

        Resume resume = new Resume();
        resume.setResumeId(3L);

        when(applicationRepository
                .findByJobJobIdAndSeekerSeekerId(1L,2L))
                .thenReturn(Optional.empty());

        when(jobRepository.findById(1L))
                .thenReturn(Optional.of(job));

        when(seekerRepository.findById(2L))
                .thenReturn(Optional.of(seeker));

        when(resumeRepository.findById(3L))
                .thenReturn(Optional.of(resume));

        Application saved = new Application();
        saved.setApplicationId(10L);

        when(applicationRepository.save(any()))
                .thenReturn(saved);

        Application result =
                applicationService.applyJob(1L,2L,3L,"Cover Letter");

        assertNotNull(result);
    }

    // -------- WITHDRAW APPLICATION --------

    @Test
    void testWithdrawApplication() {

        Application app = new Application();
        app.setApplicationId(1L);

        when(applicationRepository.findById(1L))
                .thenReturn(Optional.of(app));

        applicationService.withdrawApplication(1L,"Not interested");

        assertEquals("WITHDRAWN",app.getStatus());
    }

    // -------- UPDATE STATUS --------

    @Test
    void testUpdateStatus() {

        Application app = new Application();
        app.setApplicationId(1L);

        when(applicationRepository.findById(1L))
                .thenReturn(Optional.of(app));

        when(applicationRepository.save(app)).thenReturn(app);

        Application result =
                applicationService.updateStatus(1L,"SHORTLISTED");

        assertEquals("SHORTLISTED",result.getStatus());
    }

    // -------- GET APPLICATIONS BY SEEKER --------

    @Test
    void testGetApplicationsBySeeker() {

        Job job = new Job();
        job.setTitle("Java Developer");

        User user = new User();
        user.setEmail("test@gmail.com");

        JobSeekerProfile seeker = new JobSeekerProfile();
        seeker.setFullName("John");
        seeker.setUser(user);

        Application app = new Application();
        app.setApplicationId(1L);
        app.setJob(job);
        app.setSeeker(seeker);
        app.setStatus("APPLIED");
        app.setAppliedDate(LocalDateTime.now());

        when(applicationRepository.findBySeekerSeekerId(1L))
                .thenReturn(List.of(app));

        List<ApplicationResponseDTO> result =
                applicationService.getApplicationsBySeeker(1L);

        assertEquals(1,result.size());
    }

    // -------- GET APPLICATIONS BY JOB --------

    @Test
    void testGetApplicationsByJob() {

        Job job = new Job();
        job.setTitle("Backend Developer");

        User user = new User();
        user.setEmail("user@gmail.com");

        JobSeekerProfile seeker = new JobSeekerProfile();
        seeker.setFullName("Mike");
        seeker.setUser(user);

        Application app = new Application();
        app.setApplicationId(1L);
        app.setJob(job);
        app.setSeeker(seeker);
        app.setStatus("APPLIED");
        app.setAppliedDate(LocalDateTime.now());

        when(applicationRepository.findByJobJobId(1L))
                .thenReturn(List.of(app));

        List<ApplicationResponseDTO> result =
                applicationService.getApplicationsByJob(1L);

        assertEquals(1,result.size());
    }

    // -------- GET APPLICATIONS BY EMPLOYER --------

    @Test
    void testGetApplicationsByEmployer() {

        Job job = new Job();
        job.setJobId(1L);
        job.setTitle("Spring Boot Developer");

        User user = new User();
        user.setEmail("candidate@gmail.com");

        JobSeekerProfile seeker = new JobSeekerProfile();
        seeker.setFullName("Alex");
        seeker.setUser(user);

        Application app = new Application();
        app.setApplicationId(1L);
        app.setJob(job);
        app.setSeeker(seeker);
        app.setStatus("APPLIED");
        app.setAppliedDate(LocalDateTime.now());

        when(applicationRepository
                .findByJobEmployerEmployerId(1L))
                .thenReturn(List.of(app));

        List<EmployerApplicationDTO> result =
                applicationService.getApplicationsByEmployer(1L);

        assertEquals(1,result.size());
    }

    // -------- ADD EMPLOYER NOTES --------

    @Test
    void testAddEmployerNotes() {

        Application app = new Application();
        app.setApplicationId(1L);

        EmployerProfile employer = new EmployerProfile();
        employer.setEmployerId(1L);

        when(applicationRepository.findById(1L))
                .thenReturn(Optional.of(app));

        when(employerRepository.findById(1L))
                .thenReturn(Optional.of(employer));

        String result =
                applicationService.addEmployerNotes(1L,1L,"Good candidate");

        assertEquals("Note added successfully",result);
    }
}