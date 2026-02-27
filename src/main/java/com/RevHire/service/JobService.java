package com.RevHire.service;

import com.RevHire.dto.JobDTO;
import com.RevHire.entity.Job;

import java.util.List;

public interface JobService {

    Job createJob(Job job);

    List<JobDTO> getAllOpenJobs();

    List<JobDTO> searchJobs(
          String title,
          String location,
          Integer experience,
          String companyName,
          Double minSalary,
          Double maxSalary,
          String jobType
   );

  void closeJob(Long jobId);

  void deleteJob(Long jobId);

  List<JobDTO> getEmployerJobs(Long employerId);

    JobDTO toggleJobStatus(Long jobId);

}
