package com.RevHire.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ApplicationResponseDTO {

    private Long applicationId;
    private String jobTitle;
    private String status;
    private LocalDateTime appliedDate;

}