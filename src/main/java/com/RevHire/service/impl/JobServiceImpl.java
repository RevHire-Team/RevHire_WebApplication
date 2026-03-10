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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Service
public class JobServiceImpl implements JobService {

    private static final Logger logger = LogManager.getLogger(JobServiceImpl.class);

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private com.RevHire.repository.EmployerProfileRepository employerRepository;

    @Override
    @Transactional
    public Job createJob(Job job, Long userId) {

        logger.info("Creating job: {} for userId: {}", job.getTitle(), userId);

        com.RevHire.entity.EmployerProfile employer = employerRepository.findByUserUserId(userId)
                .orElseThrow(() -> {
                    logger.error("Employer Profile not found for userId: {}", userId);
                    return new RuntimeException("Employer Profile not found");
                });

        job.setEmployer(employer);
        job.setStatus("OPEN");
        job.setActive(true);

        Job savedJob = jobRepository.save(job);

        logger.info("Job saved successfully with ID: {}", savedJob.getJobId());

        return savedJob;
    }

    @Override
    public List<JobDTO> getAllOpenJobs() {

        logger.info("Fetching all OPEN jobs");

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
    public List<JobDTO> searchJobs(String title,
                                   String location,
                                   Integer experience,
                                   String education,
                                   Double minSalary,
                                   Double maxSalary,
                                   String jobType) {

        logger.info("Searching jobs with filters: title={}, location={}, experience={}",
                title, location, experience);

        return jobRepository.advancedSearch(
                        title,
                        location,
                        experience,
                        education,
                        minSalary,
                        maxSalary,
                        jobType
                )
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
    public void closeJob(Long jobId) {

        logger.info("Closing job with ID: {}", jobId);

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> {
                    logger.error("Job not found with ID: {}", jobId);
                    return new RuntimeException("Job not found");
                });

        job.setStatus("CLOSED");

        jobRepository.save(job);

        logger.info("Job closed successfully with ID: {}", jobId);
    }

    @Override
    @Transactional
    public void deleteJob(Long jobId) {

        logger.info("Deleting job with ID: {}", jobId);

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> {
                    logger.error("Job not found while deleting with ID: {}", jobId);
                    return new RuntimeException("Job not found");
                });

        jobRepository.delete(job);

        logger.info("Job deleted successfully with ID: {}", jobId);
    }

    @Override
    public List<JobDTO> getJobsByUserId(Long userId) {

        logger.info("Fetching jobs for userId: {}", userId);

        com.RevHire.entity.EmployerProfile employer = employerRepository.findByUserUserId(userId)
                .orElseThrow(() -> {
                    logger.error("Employer Profile not found for userId: {}", userId);
                    return new RuntimeException("Employer Profile not found");
                });

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
    public List<JobDTO> getEmployerJobsSorted(Long userId, String sort) {

        logger.info("Fetching employer jobs sorted by: {} for userId: {}", sort, userId);

        com.RevHire.entity.EmployerProfile employer =
                employerRepository.findByUserUserId(userId)
                        .orElseThrow(() -> {
                            logger.error("Employer Profile not found for userId: {}", userId);
                            return new RuntimeException("Employer Profile not found");
                        });

        List<Job> jobs;

        if ("name".equalsIgnoreCase(sort)) {

            jobs = jobRepository
                    .findByEmployerEmployerIdOrderByTitleAsc(employer.getEmployerId());

        } else if ("recent".equalsIgnoreCase(sort)) {

            jobs = jobRepository
                    .findByEmployerEmployerIdOrderByJobIdDesc(employer.getEmployerId());

        } else {

            jobs = jobRepository
                    .findByEmployerEmployerId(employer.getEmployerId());
        }

        return jobs.stream().map(job -> new JobDTO(
                job.getJobId(),
                job.getTitle(),
                job.getLocation(),
                job.getSalaryMin(),
                job.getSalaryMax(),
                job.getJobType(),
                job.getStatus(),
                job.getEmployer().getCompanyName()
        )).toList();
    }

    @Override
    public JobDTO toggleJobStatus(Long jobId) {

        logger.info("Toggling job status for jobId: {}", jobId);

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> {
                    logger.error("Job not found with ID: {}", jobId);
                    return new RuntimeException("Job not found");
                });

        boolean newActiveState = !job.getActive();
        job.setActive(newActiveState);

        job.setStatus(newActiveState ? "OPEN" : "CLOSED");

        Job savedJob = jobRepository.save(job);

        logger.info("Job status toggled successfully for jobId: {}", jobId);

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

        logger.info("Fetching job details for jobId: {}", jobId);

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> {
                    logger.error("Job not found with ID: {}", jobId);
                    return new RuntimeException("Job not found with ID: " + jobId);
                });

        return new JobDTO(
                job.getJobId(),
                job.getTitle(),
                job.getLocation(),
                job.getSalaryMin(),
                job.getSalaryMax(),
                job.getJobType(),
                job.getStatus(),
                job.getEmployer().getCompanyName()
        );
    }

    @Override
    @Transactional
    public JobDTO updateJob(Long jobId, Job updatedJob) {

        logger.info("Updating job with ID: {}", jobId);

        Job existingJob = jobRepository.findById(jobId)
                .orElseThrow(() -> {
                    logger.error("Job not found for update with ID: {}", jobId);
                    return new RuntimeException("Job not found");
                });

        existingJob.setTitle(updatedJob.getTitle());
        existingJob.setLocation(updatedJob.getLocation());
        existingJob.setJobType(updatedJob.getJobType());
        existingJob.setSalaryMin(updatedJob.getSalaryMin());
        existingJob.setSalaryMax(updatedJob.getSalaryMax());
        existingJob.setDescription(updatedJob.getDescription());
        existingJob.setExperienceRequired(updatedJob.getExperienceRequired());
        existingJob.setEducationRequired(updatedJob.getEducationRequired());

        Job savedJob = jobRepository.save(existingJob);

        logger.info("Job updated successfully with ID: {}", jobId);

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

    // ================= RECOMMENDED JOBS =================

    @Override
    public List<JobDTO> getRecommendedJobs(String skill) {

        logger.info("Fetching recommended jobs for skill: {}", skill);

        List<Job> jobs = jobRepository.findRecommendedJobs(skill);

        return jobs.stream()
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
}