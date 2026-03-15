package com.RevHire.dto;

import lombok.Data;

@Data
public class EmployerProfileDTO {

    private String companyName;
    private String industry;
    private Integer companySize;
    private String description;
    private String ContactEmail;
    private String website;
    private String location;
}