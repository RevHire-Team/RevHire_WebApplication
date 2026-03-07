package com.RevHire.service;

import com.RevHire.entity.*;
import com.RevHire.repository.*;
import com.RevHire.service.impl.ApplicationServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ApplicationServiceTest {

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private JobRepository jobRepository;

    @Mock
    private JobSeekerProfileRepository seekerRepository;

    @Mock
    private ResumeRepository resumeRepository;

    @InjectMocks
    private ApplicationServiceImpl applicationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testApplyJob() {

        Job job = new Job();
        job.setJobId(1L);

        JobSeekerProfile seeker = new JobSeekerProfile();
        seeker.setSeekerId(2L);

        Resume resume = new Resume();
        resume.setResumeId(3L);

        when(applicationRepository.findByJobJobIdAndSeekerSeekerId(1L,2L))
                .thenReturn(Optional.empty());

        when(jobRepository.findById(1L))
                .thenReturn(Optional.of(job));

        when(seekerRepository.findById(2L))
                .thenReturn(Optional.of(seeker));

        when(resumeRepository.findById(3L))
                .thenReturn(Optional.of(resume));

        Application saved = new Application();
        saved.setApplicationId(10L);

        when(applicationRepository.save(any(Application.class)))
                .thenReturn(saved);

        Application result = applicationService.applyJob(
                1L,2L,3L,"cover letter"
        );

        assertNotNull(result);

        verify(applicationRepository,times(1))
                .save(any(Application.class));
    }
}