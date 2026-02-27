package com.RevHire.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Data
public class JobDTO {

    private Long jobId;
    private String title;
    private String location;
    private BigDecimal salaryMin;
    private BigDecimal salaryMax;
    private String jobType;
    private String status;
    private String companyName;

    public JobDTO(Long jobId,
                  String title,
                  String location,
                  BigDecimal salaryMin,
                  BigDecimal salaryMax,
                  String jobType,
                  String status,
                  String companyName) {
        this.jobId = jobId;
        this.title = title;
        this.location = location;
        this.salaryMin = salaryMin;
        this.salaryMax = salaryMax;
        this.jobType = jobType;
        this.status = status;
        this.companyName = companyName;
    }
}