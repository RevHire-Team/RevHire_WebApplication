package com.RevHire.dto;

import lombok.Data;

@Data
public class ProfileUpdateDTO {
    private String fullName;
    private String phone;
    private String location;
    private String jobTitle; // Maps to Resume.objective
    private String skills;    // Comma-separated string
    private String education; // Flattened string for now
    private String experienceSummary;
}