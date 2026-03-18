package com.RevHire.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequestDTO {
    private String email;
    private String password;
    private String role; // JOB_SEEKER, EMPLOYER
    private String fullName; // For Seeker
    private String companyName; // For Employer
    private String securityQuestion;
    private String securityAnswer;

}
