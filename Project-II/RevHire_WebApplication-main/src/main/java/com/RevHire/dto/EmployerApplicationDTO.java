package com.RevHire.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor // Added for better compatibility
public class EmployerApplicationDTO {
    private Long applicationId;
    private String jobTitle;
    private Long jobId;
    private String applicantName;
    private String applicantEmail;
    private String status;
    private LocalDateTime appliedDate;
    private Long resumeId;
//
//    // ADD THESE FIELDS FOR FILTRATION
//    private String skills;
//    private String education;
//    private Integer experience;
}