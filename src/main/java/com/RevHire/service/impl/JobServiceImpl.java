package com.RevHire.service.impl;

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
    public List<JobDTO> getJobsByUserId(Long userId) {
        // 1. Find the employer profile first
        com.RevHire.entity.EmployerProfile employer = employerRepository.findByUserUserId(userId)
                .orElseThrow(() -> new RuntimeException("Employer Profile not found"));

        // 2. Now fetch jobs using the actual Employer ID
        return jobRepository.findByEmployerEmployerId(employer.getEmployerId())
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

        // 1. Toggle the boolean
        boolean newActiveState = !job.getActive();
        job.setActive(newActiveState);

        // 2. Synchronize the Status string
        // If active is true -> OPEN, if false -> CLOSED
        job.setStatus(newActiveState ? "OPEN" : "CLOSED");

        Job savedJob = jobRepository.save(job);

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

    @Override
    public JobDTO getJobById(Long jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found with ID: " + jobId));

        // Convert Entity to DTO
        return new JobDTO(
                job.getJobId(),
                job.getTitle(),
                job.getLocation(),
                job.getSalaryMin(),
                job.getSalaryMax(),
                job.getJobType(),
                job.getStatus(),
                job.getEmployer().getCompanyName()
                // If your JobDTO has description/experience, add them here
        );
    }

    @Override
    @Transactional
    public JobDTO updateJob(Long jobId, Job updatedJob) {
        // 1. Fetch existing job
        Job existingJob = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        // 2. Update the fields
        existingJob.setTitle(updatedJob.getTitle());
        existingJob.setLocation(updatedJob.getLocation());
        existingJob.setJobType(updatedJob.getJobType());
        existingJob.setSalaryMin(updatedJob.getSalaryMin());
        existingJob.setSalaryMax(updatedJob.getSalaryMax());
        existingJob.setDescription(updatedJob.getDescription());
        existingJob.setExperienceRequired(updatedJob.getExperienceRequired());
        existingJob.setEducationRequired(updatedJob.getEducationRequired());

        // Note: We usually don't update the Employer or JobID

        // 3. Save the changes
        Job savedJob = jobRepository.save(existingJob);

        // 4. Return updated DTO
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