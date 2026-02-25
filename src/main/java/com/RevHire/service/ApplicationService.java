package com.RevHire.service;

import com.RevHire.dto.ApplicationResponseDTO;
import com.RevHire.entity.Application;

import java.time.LocalDateTime;
import java.util.List;

public interface ApplicationService {

    Application applyJob(Long jobId, Long seekerId, Long resumeId, String coverLetter);

    List<ApplicationResponseDTO> getApplicationsBySeeker(Long seekerId);

    void withdrawApplication(Long applicationId, String reason);

    void updateStatus(Long applicationId, String status);
}
