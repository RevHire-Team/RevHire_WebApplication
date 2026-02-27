package com.RevHire.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class EmployerApplicationDTO {

    private Long applicationId;
    private String jobTitle;
    private Long jobId;

    private String applicantName;
    private String applicantEmail;

    private String status;
    private LocalDateTime appliedDate;
}