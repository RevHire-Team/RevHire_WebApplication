package com.RevHire.service;

import com.RevHire.entity.*;
import java.util.List;

public interface ResumeService {

    Resume createResume(Resume resume);

    Resume updateResume(Long resumeId, Resume resume);

    void deleteResume(Long resumeId);

    // Education
    ResumeEducation addEducation(ResumeEducation education);

    List<ResumeEducation> getEducationByResume(Long resumeId);
    void deleteEducation(Long educationId);

    // Experience
    ResumeExperience addExperience(ResumeExperience experience);

    List<ResumeExperience> getExperienceByResume(Long resumeId);
    void deleteExperience(Long experienceId);

    // Skills
    ResumeSkill addSkill(ResumeSkill skill);
    List<ResumeSkill> getSkillsByResume(Long resumeId);
    void deleteSkill(Long skillId);
}