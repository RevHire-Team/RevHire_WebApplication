package com.RevHire.service.impl;

import com.RevHire.dto.JobDTO;
import com.RevHire.entity.EmployerProfile;
import com.RevHire.entity.Job;
import com.RevHire.repository.EmployerProfileRepository;
import com.RevHire.repository.JobRepository;

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

public class JobServiceImplTest {

    @Mock
    private JobRepository jobRepository;

    @Mock
    private EmployerProfileRepository employerRepository;

    @InjectMocks
    private JobServiceImpl jobService;

    private Job job;
    private EmployerProfile employer;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);

        employer = new EmployerProfile();
        employer.setEmployerId(1L);
        employer.setCompanyName("ABC Tech");

        job = new Job();
        job.setJobId(1L);
        job.setTitle("Java Developer");
        job.setLocation("Bangalore");
        job.setSalaryMin(BigDecimal.valueOf(30000));
        job.setSalaryMax(BigDecimal.valueOf(60000));
        job.setJobType("FULL_TIME");
        job.setStatus("OPEN");
        job.setActive(true);
        job.setEmployer(employer);
    }

    // ================= CREATE JOB =================

    @Test
    void testCreateJob() {

        when(employerRepository.findByUserUserId(1L))
                .thenReturn(Optional.of(employer));

        when(jobRepository.save(job)).thenReturn(job);

        Job result = jobService.createJob(job, 1L);

        assertNotNull(result);
        assertEquals("OPEN", result.getStatus());

        verify(jobRepository).save(job);
    }

    @Test
    void testCreateJobEmployerNotFound() {

        when(employerRepository.findByUserUserId(1L))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> jobService.createJob(job, 1L));
    }

    // ================= GET ALL OPEN JOBS =================

    @Test
    void testGetAllOpenJobs() {

        when(jobRepository.findByStatus("OPEN"))
                .thenReturn(List.of(job));

        List<JobDTO> result = jobService.getAllOpenJobs();

        assertEquals(1, result.size());
        assertEquals("Java Developer", result.get(0).getTitle());
    }

    // ================= SEARCH JOBS =================

    @Test
    void testSearchJobs() {

        when(jobRepository.advancedSearch(
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any()
        )).thenReturn(List.of(job));

        List<JobDTO> jobs =
                jobService.searchJobs(
                        "Java",
                        "Bangalore",
                        2,
                        "BTech",
                        20000.0,
                        70000.0,
                        "FULL_TIME"
                );

        assertEquals(1, jobs.size());
        assertEquals("Java Developer", jobs.get(0).getTitle());
    }

    // ================= CLOSE JOB =================

    @Test
    void testCloseJob() {

        when(jobRepository.findById(1L))
                .thenReturn(Optional.of(job));

        jobService.closeJob(1L);

        assertEquals("CLOSED", job.getStatus());
        verify(jobRepository).save(job);
    }

    // ================= DELETE JOB =================

    @Test
    void testDeleteJob() {

        when(jobRepository.findById(1L))
                .thenReturn(Optional.of(job));

        jobService.deleteJob(1L);

        verify(jobRepository).delete(job);
    }

    // ================= GET JOBS BY USER =================

    @Test
    void testGetJobsByUserId() {

        when(employerRepository.findByUserUserId(1L))
                .thenReturn(Optional.of(employer));

        when(jobRepository.findByEmployerEmployerId(1L))
                .thenReturn(List.of(job));

        List<JobDTO> jobs = jobService.getJobsByUserId(1L);

        assertEquals(1, jobs.size());
        assertEquals("Java Developer", jobs.get(0).getTitle());
    }

    // ================= TOGGLE JOB STATUS =================

    @Test
    void testToggleJobStatus() {

        when(jobRepository.findById(1L))
                .thenReturn(Optional.of(job));

        when(jobRepository.save(job))
                .thenReturn(job);

        JobDTO dto = jobService.toggleJobStatus(1L);

        assertEquals("CLOSED", dto.getStatus());
    }

    // ================= GET JOB BY ID =================

    @Test
    void testGetJobById() {

        when(jobRepository.findById(1L))
                .thenReturn(Optional.of(job));

        JobDTO result = jobService.getJobById(1L);

        assertNotNull(result);
        assertEquals("Java Developer", result.getTitle());
    }

    // ================= UPDATE JOB =================

    @Test
    void testUpdateJob() {

        Job updatedJob = new Job();
        updatedJob.setTitle("Senior Java Developer");
        updatedJob.setLocation("Hyderabad");
        updatedJob.setJobType("FULL_TIME");
        updatedJob.setSalaryMin(BigDecimal.valueOf(50000));
        updatedJob.setSalaryMax(BigDecimal.valueOf(90000));
        updatedJob.setDescription("Backend role");
        updatedJob.setExperienceRequired(5);
        updatedJob.setEducationRequired("BTech");

        when(jobRepository.findById(1L))
                .thenReturn(Optional.of(job));

        when(jobRepository.save(any(Job.class)))
                .thenReturn(job);

        JobDTO result = jobService.updateJob(1L, updatedJob);

        assertNotNull(result);
        verify(jobRepository).save(job);
    }

}