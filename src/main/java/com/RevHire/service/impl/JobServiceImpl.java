package com.RevHire.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.RevHire.entity.Job;
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

    @Override
    public List<Job> getAllOpenJobs() {
        return jobRepository.findByStatus("OPEN");
    }

    @Override
    public List<Job> searchJobs(String location, String title, String jobType) {
        return jobRepository.advancedSearch(location, title, jobType);
    }

    @Override
    public void closeJob(Long jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        job.setStatus("CLOSED");
        jobRepository.save(job);
    }
}