package com.RevHire.service;

import com.RevHire.entity.*;
import org.springframework.core.io.Resource;import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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

    ResponseEntity<Resource> downloadResumeFile(Long userId);

    // Skills
    void saveSkills(Long resumeId, List<ResumeSkill> skills);

    // File Management
    void saveResumeFile(Long userId, MultipartFile file) throws IOException;

    Resume getResumeByUserId(Long userId);

    @Transactional
    void deleteResumeFile(Long fileId);

    byte[] getResumeFileBytes(Long userId);
}