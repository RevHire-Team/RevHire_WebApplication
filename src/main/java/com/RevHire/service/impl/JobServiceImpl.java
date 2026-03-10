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

        System.out.println("Creating job: " + job.getTitle() + " for user: " + userId);

        com.RevHire.entity.EmployerProfile employer = employerRepository.findByUserUserId(userId)
                .orElseThrow(() -> new RuntimeException("Employer Profile not found"));

        job.setEmployer(employer);
        job.setStatus("OPEN");
        job.setActive(true);

        Job savedJob = jobRepository.save(job);
        System.out.println("Job saved with ID: " + savedJob.getJobId());

        return savedJob;
    }


    @Override
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
    public List<JobDTO> searchJobs(String title,
                                   String location,
                                   Integer experience,
                                   String education,
                                   Double minSalary,
                                   Double maxSalary,
                                   String jobType) {

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

        com.RevHire.entity.EmployerProfile employer = employerRepository.findByUserUserId(userId)
                .orElseThrow(() -> new RuntimeException("Employer Profile not found"));

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

        com.RevHire.entity.EmployerProfile employer =
                employerRepository.findByUserUserId(userId)
                        .orElseThrow(() -> new RuntimeException("Employer Profile not found"));

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

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        boolean newActiveState = !job.getActive();
        job.setActive(newActiveState);

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

        Job existingJob = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        existingJob.setTitle(updatedJob.getTitle());
        existingJob.setLocation(updatedJob.getLocation());
        existingJob.setJobType(updatedJob.getJobType());
        existingJob.setSalaryMin(updatedJob.getSalaryMin());
        existingJob.setSalaryMax(updatedJob.getSalaryMax());
        existingJob.setDescription(updatedJob.getDescription());
        existingJob.setExperienceRequired(updatedJob.getExperienceRequired());
        existingJob.setEducationRequired(updatedJob.getEducationRequired());

        Job savedJob = jobRepository.save(existingJob);

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
