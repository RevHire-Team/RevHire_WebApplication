package com.RevHire.service;

import com.RevHire.entity.Application;

import java.util.List;

public interface ApplicationService {

    Application applyJob(Long jobId, Long seekerId, Long resumeId, String coverLetter);

    List<Application> getApplicationsBySeeker(Long seekerId);

    void withdrawApplication(Long applicationId, String reason);

    void updateStatus(Long applicationId, String status);
}
