package com.RevHire.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JobRequestDTO {

    private Long employerId;
    private String title;
    private String description;
    private Integer experienceRequired;
    private String educationRequired;
    private String location;
    private Double salaryMin;
    private Double salaryMax;
    private String jobType;
    private Integer openings;
}