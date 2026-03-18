package com.RevHire.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ApplicationDetailsDTO {

    private Long applicationId;
    private String applicantName;
    private String applicantEmail;
    private String jobTitle;
    private String status;
    private LocalDateTime appliedDate;

    private Long resumeId;      // builder resume
    private Long resumeFileId;  // uploaded resume

    public ApplicationDetailsDTO(Long applicationId,
                                 String applicantName,
                                 String applicantEmail,
                                 String jobTitle,
                                 String status,
                                 LocalDateTime appliedDate,
                                 Long resumeId,
                                 Long resumeFileId) {

        this.applicationId = applicationId;
        this.applicantName = applicantName;
        this.applicantEmail = applicantEmail;
        this.jobTitle = jobTitle;
        this.status = status;
        this.appliedDate = appliedDate;
        this.resumeId = resumeId;
        this.resumeFileId = resumeFileId;
    }
}