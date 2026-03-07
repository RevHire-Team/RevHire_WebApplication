package com.RevHire.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployerDashboardDTO {

    private Long totalJobs;
    private Long activeJobs;
    private Long totalApplications;
    private Long pendingReviews;
    private double profileCompletionPercentage;
}
