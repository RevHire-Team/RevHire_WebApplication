package com.RevHire.service;

import com.RevHire.dto.JobDTO;
import com.RevHire.entity.Job;
import com.RevHire.entity.EmployerProfile;
import com.RevHire.repository.JobRepository;
import com.RevHire.service.impl.JobServiceImpl;

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

class JobServiceTest {

    @Mock
    private JobRepository jobRepository;

    @InjectMocks
    private JobServiceImpl jobService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateJob() {

        Job job = new Job();
        job.setTitle("Java Developer");

        when(jobRepository.save(job)).thenReturn(job);

        Job result = jobService.createJob(job);

        assertEquals("OPEN", result.getStatus());
        verify(jobRepository,times(1)).save(job);
    }

    @Test
    void testGetAllOpenJobs() {

        EmployerProfile employer = new EmployerProfile();
        employer.setCompanyName("Google");

        Job job = new Job();
        job.setJobId(1L);
        job.setTitle("Backend Developer");
        job.setLocation("Bangalore");
        job.setSalaryMin(BigDecimal.valueOf(50000));
        job.setSalaryMax(BigDecimal.valueOf(100000));
        job.setJobType("FULL_TIME");
        job.setStatus("OPEN");
        job.setEmployer(employer);

        when(jobRepository.findByStatus("OPEN")).thenReturn(List.of(job));

        List<JobDTO> jobs = jobService.getAllOpenJobs();

        assertEquals(1, jobs.size());
    }

    @Test
    void testSearchJobs() {

        JobDTO dto = new JobDTO(
                1L,"Java Developer","Hyderabad",
                BigDecimal.valueOf(50000),
                BigDecimal.valueOf(90000),
                "FULL_TIME","OPEN","TCS"
        );

        when(jobRepository.advancedSearch(
                any(),any(),any(),any(),any(),any(),any()))
                .thenReturn(List.of(dto));

        List<JobDTO> result = jobService.searchJobs(
                "Java","Hyderabad",2,"TCS",50000.0,90000.0,"FULL_TIME"
        );

        assertEquals(1,result.size());
    }

    @Test
    void testCloseJob() {

        Job job = new Job();
        job.setJobId(1L);
        job.setStatus("OPEN");

        when(jobRepository.findById(1L))
                .thenReturn(Optional.of(job));

        jobService.closeJob(1L);

        assertEquals("CLOSED", job.getStatus());
        verify(jobRepository).save(job);
    }

    @Test
    void testDeleteJob() {

        Job job = new Job();
        job.setJobId(1L);

        when(jobRepository.findById(1L))
                .thenReturn(Optional.of(job));

        jobService.deleteJob(1L);

        verify(jobRepository).delete(job);
    }

    @Test
    void testToggleJobStatus() {

        EmployerProfile employer = new EmployerProfile();
        employer.setCompanyName("Infosys");

        Job job = new Job();
        job.setJobId(1L);
        job.setActive(true);
        job.setEmployer(employer);

        when(jobRepository.findById(1L))
                .thenReturn(Optional.of(job));

        when(jobRepository.save(job)).thenReturn(job);

        JobDTO dto = jobService.toggleJobStatus(1L);

        assertNotNull(dto);
        verify(jobRepository).save(job);
    }
}