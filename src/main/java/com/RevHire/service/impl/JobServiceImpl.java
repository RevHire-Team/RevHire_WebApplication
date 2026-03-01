package com.RevHire.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.RevHire.entity.Job;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.RevHire.dto.JobDTO;
import com.RevHire.repository.JobRepository;
import com.RevHire.service.JobService;

@Service
public class JobServiceImpl implements JobService {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private com.RevHire.repository.EmployerProfileRepository employerRepository;

    @Override
    @Transactional
    public Job createJob(Job job, Long userId) {
        // DEBUG: Print to console to see if fields are null
        System.out.println("Creating job: " + job.getTitle() + " for user: " + userId);

        com.RevHire.entity.EmployerProfile employer = employerRepository.findByUserUserId(userId)
                .orElseThrow(() -> new RuntimeException("Employer Profile not found"));

        job.setEmployer(employer);
        job.setStatus("OPEN");
        job.setActive(true);

        // The result of this save is what actually goes to the DB
        Job savedJob = jobRepository.save(job);
        System.out.println("Job saved with ID: " + savedJob.getJobId());

        return savedJob;
    }

    public List<JobDTO> getAllOpenJobs() {

        return jobRepository.findByStatus("OPEN")
                .stream()
                .map(job -> new JobDTO(
                        job.getJobId(),
                        job.getTitle(),
                        job.getLocation(),
                        job.getSalaryMin(),
                        job.getSalaryMax(),
                        job.getJobType(),
                        job.getStatus(),
                        job.getEmployer().getCompanyName()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<JobDTO> searchJobs(
            String title,
            String location,
            Integer experience,
            String companyName,
            Double minSalary,
            Double maxSalary,
            String jobType) {

        return jobRepository.advancedSearch(
                title,
                location,
                experience,
                companyName,
                minSalary,
                maxSalary,
                jobType
        );
    }

    @Override
    public void closeJob(Long jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        job.setStatus("CLOSED");
        jobRepository.save(job);
    }

    @Override
    @Transactional
    public void deleteJob(Long jobId) {

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        jobRepository.delete(job);
    }

    @Override
    public List<JobDTO> getEmployerJobs(Long employerId) {

        return jobRepository.findByEmployerEmployerId(employerId)
                .stream()
                .map(job -> new JobDTO(
                        job.getJobId(),
                        job.getTitle(),
                        job.getLocation(),
                        job.getSalaryMin(),
                        job.getSalaryMax(),
                        job.getJobType(),
                        job.getStatus(),
                        job.getEmployer().getCompanyName()
                ))
                .toList();
    }

    @Override
    public JobDTO toggleJobStatus(Long jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        // Toggle active status
        job.setActive(!job.getActive());
        Job savedJob = jobRepository.save(job);

        // Map Job to JobDTO
        return new JobDTO(
                savedJob.getJobId(),
                savedJob.getTitle(),
                savedJob.getLocation(),
                savedJob.getSalaryMin(),
                savedJob.getSalaryMax(),
                savedJob.getJobType(),
                savedJob.getStatus(),
                savedJob.getEmployer().getCompanyName()
        );
    }
}