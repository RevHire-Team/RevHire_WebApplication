package com.RevHire.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ApplicationResponseDTO {

    private Long id;
    private String applicantName;
    private String applicantEmail;
    private String jobTitle;
    private String status;
    private LocalDateTime appliedDate;

    public ApplicationResponseDTO(Long id, String applicantName,
                                  String applicantEmail,
                                  String jobTitle, String status,
                                  LocalDateTime appliedDate) {
        this.id = id;
        this.applicantName = applicantName;
        this.applicantEmail = applicantEmail;
        this.jobTitle = jobTitle;
        this.status = status;
        this.appliedDate = appliedDate;
    }
}