package com.RevHire.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ResumeSaveDTO {

    private String objective;

    private List<EducationDTO> educations;
    private List<ExperienceDTO> experiences;
    private List<ProjectDTO> projects;
    private List<CertificationDTO> certifications;
    private List<String> skills;

    // getters and setters
}