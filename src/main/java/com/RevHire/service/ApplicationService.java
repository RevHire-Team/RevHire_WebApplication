package com.RevHire.service;

import com.RevHire.dto.ApplicationResponseDTO;
import com.RevHire.entity.Application;

import java.time.LocalDateTime;
import java.util.List;

public interface ApplicationService {

    Application applyJob(Long jobId, Long seekerId, Long resumeId, String coverLetter);

    public List<ApplicationResponseDTO> getApplicationsBySeeker(Long seekerId);

    void withdrawApplication(Long applicationId, String reason);

    List<ApplicationResponseDTO> getApplicationsByJob(Long jobId);

    List<Application> getApplicationsByEmployer(Long employerId);

    Application updateStatus(Long applicationId, String status);

//    Application addEmployerNotes(Long applicationId, String notes);
}
