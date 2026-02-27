package com.RevHire.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.RevHire.entity.Job;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.RevHire.dto.JobDTO;
import com.RevHire.repository.JobRepository;
import com.RevHire.service.JobService;

@Service
public class JobServiceImpl implements JobService {

    @Autowired
    private JobRepository jobRepository;

    @Override
    public Job createJob(Job job) {
        job.setStatus("OPEN");
        return jobRepository.save(job);
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
    public void deleteJob(Long jobId) {
        jobRepository.deleteById(jobId);
    }

    @Override
    public List<Job> getEmployerJobs(Long employerId) {
        return jobRepository.findByEmployerEmployerId(employerId);
    }

    @Override
    public Job toggleJobStatus(Long jobId) {

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        job.setActive(!job.getActive());
        return jobRepository.save(job);
    }
}