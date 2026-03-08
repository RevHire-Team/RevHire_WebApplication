package com.RevHire.service;

import com.RevHire.dto.JobDTO;
import com.RevHire.entity.Job;

import java.util.List;

public interface JobService {

    Job createJob(Job job, Long userId);

    List<JobDTO> getAllOpenJobs();

    void closeJob(Long jobId);

    void deleteJob(Long jobId);

    List<JobDTO> getJobsByUserId(Long userId);

    JobDTO toggleJobStatus(Long jobId);

    JobDTO getJobById(Long jobId);

    JobDTO updateJob(Long jobId, Job updatedJob);

    List<JobDTO> getEmployerJobsSorted(Long userId, String sort);

    List<JobDTO> searchJobs(String title,
                            String location,
                            Integer experience,
                            String education,
                            Double minSalary,
                            Double maxSalary,
                            String jobType);
}
