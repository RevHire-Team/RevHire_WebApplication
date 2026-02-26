package com.RevHire.service;

import com.RevHire.entity.Job;

import java.util.List;

public interface JobService {

    Job createJob(Job job);

    List<Job> getAllOpenJobs();

    List<Job> searchJobs(String location, String title, String jobType);

    void closeJob(Long jobId);
}
