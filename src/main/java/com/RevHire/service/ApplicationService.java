package com.RevHire.service;

import com.RevHire.dto.ApplicationResponseDTO;
import com.RevHire.dto.EmployerApplicationDTO;
import com.RevHire.entity.Application;

import java.time.LocalDateTime;
import java.util.List;

public interface ApplicationService {

    public Application applyJob(Long jobId,
                                Long userId,
                                Long resumeId,
                                Long fileId,
                                String coverLetter);

    public List<ApplicationResponseDTO> getApplicationsBySeeker(Long seekerId);

    void withdrawApplication(Long applicationId, String reason);

    List<ApplicationResponseDTO> getApplicationsByJob(Long jobId);

    List<EmployerApplicationDTO> getApplicationsByEmployer(Long employerId);


    Application updateStatus(Long applicationId, String status);

    String addEmployerNotes(Long applicationId,
                            Long employerId,
                            String noteText);
}
