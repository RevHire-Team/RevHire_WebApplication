package com.RevHire.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EmployerDashboardDTO {

    private Long totalJobs;
    private Long activeJobs;
    private Long totalApplications;
    private Long pendingReviews;
}
