package com.RevHire.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.RevHire.dto.JobDTO;
import com.RevHire.entity.EmployerProfile;
import com.RevHire.entity.Job;
import com.RevHire.repository.EmployerProfileRepository;
import com.RevHire.repository.JobRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class JobServiceImplTest {

    @Mock
    private JobRepository jobRepository;

    @Mock
    private EmployerProfileRepository employerRepository;

    @InjectMocks
    private JobServiceImpl jobService;

    // This solves the "Cannot resolve symbol" error
    @Captor
    private ArgumentCaptor<Job> jobCaptor;

    private Job job;
    private EmployerProfile employer;
    private final Long userId = 1L;
    private final Long jobId = 10L;

    @BeforeEach
    void setUp() {
        employer = new EmployerProfile();
        employer.setEmployerId(100L);
        employer.setCompanyName("Tech Corp");

        job = new Job();
        job.setJobId(jobId);
        job.setTitle("Software Engineer");
        job.setStatus("OPEN");
        job.setEmployer(employer);
    }

    @Test
    void testUpdateJob_Success() {
        // Arrange
        Job updatedData = new Job();
        updatedData.setTitle("Senior Engineer");
        updatedData.setLocation("New York");

        when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));
        when(jobRepository.save(any(Job.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        JobDTO result = jobService.updateJob(jobId, updatedData);

        // Assert
        assertEquals("Senior Engineer", result.getTitle());
        verify(jobRepository).save(jobCaptor.capture());

        // Verify the internal state of the object passed to save()
        Job capturedJob = jobCaptor.getValue();
        assertEquals("Senior Engineer", capturedJob.getTitle());
        assertEquals("New York", capturedJob.getLocation());
    }

    @Test
    void testSearchJobs_Coverage() {
        // Arrange
        when(jobRepository.advancedSearch(any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(Collections.singletonList(job));

        // Act
        List<JobDTO> results = jobService.searchJobs("Java", "Remote", 2, "Bachelors", 50000.0, 100000.0, "Full-time");

        // Assert
        assertNotNull(results);
        assertEquals(1, results.size());
        verify(jobRepository).advancedSearch("Java", "Remote", 2, "Bachelors", 50000.0, 100000.0, "Full-time");
    }

    @Test
    void testGetEmployerJobsSorted_RecentBranch() {
        // Arrange
        when(employerRepository.findByUserUserId(userId)).thenReturn(Optional.of(employer));
        when(jobRepository.findByEmployerEmployerIdOrderByJobIdDesc(100L))
                .thenReturn(Collections.singletonList(job));

        // Act
        jobService.getEmployerJobsSorted(userId, "recent");

        // Assert
        verify(jobRepository).findByEmployerEmployerIdOrderByJobIdDesc(100L);
    }

    @Test
    void testGetEmployerJobsSorted_DefaultBranch() {
        // Arrange
        when(employerRepository.findByUserUserId(userId)).thenReturn(Optional.of(employer));
        when(jobRepository.findByEmployerEmployerId(100L))
                .thenReturn(Collections.singletonList(job));

        // Act
        jobService.getEmployerJobsSorted(userId, "none"); // Triggers the 'else' block

        // Assert
        verify(jobRepository).findByEmployerEmployerId(100L);
    }

    @Test
    void testCloseJob_ThrowsException() {
        // Arrange
        when(jobRepository.findById(jobId)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> jobService.closeJob(jobId));
        assertEquals("Job not found", exception.getMessage());
    }
}