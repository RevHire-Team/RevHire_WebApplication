package com.RevHire.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResumeFileDTO {
    private Long fileId;
    private Long resumeId; // Use simple ID instead of Entity reference
    private String fileName;
    private String filePath;
    private String fileType;
    private Long fileSize;
}