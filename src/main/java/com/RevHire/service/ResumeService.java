package com.RevHire.service;

import com.RevHire.entity.*;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import jakarta.transaction.Transactional;

import java.io.IOException;
import java.util.List;

public interface ResumeService {

    // ================= RESUME =================

    Resume createResume(Resume resume);

    Resume updateResume(Long resumeId, Resume resume);

    void deleteResume(Long resumeId);

    Resume getResumeByUserId(Long userId);


    // ================= EDUCATION =================

    ResumeEducation addEducation(ResumeEducation education);

    List<ResumeEducation> getEducationByResume(Long resumeId);

    void deleteEducation(Long educationId);


    // ================= EXPERIENCE =================

    ResumeExperience addExperience(ResumeExperience experience);

    List<ResumeExperience> getExperienceByResume(Long resumeId);

    void deleteExperience(Long experienceId);


    // ================= SKILLS =================

    ResumeSkill addSkill(ResumeSkill skill);

    List<ResumeSkill> getSkillsByResume(Long resumeId);

    void deleteSkill(Long skillId);

    void saveSkills(Long resumeId, List<ResumeSkill> skills);


    // ================= CERTIFICATIONS =================

    ResumeCertification addCertification(Long resumeId, ResumeCertification cert);

    List<ResumeCertification> getCertifications(Long resumeId);

    void deleteCertification(Long certificationId);


    // ================= PROJECTS =================

    ResumeProject addProject(Long resumeId, ResumeProject project);

    List<ResumeProject> getProjects(Long resumeId);

    void deleteProject(Long projectId);


    // ================= FILE MANAGEMENT =================

    void saveResumeFile(Long userId, MultipartFile file) throws IOException;

    ResponseEntity<Resource> downloadResumeFile(Long userId);

    byte[] getResumeFileBytes(Long userId);

    ResumeFile getResumeFile(Long fileId);

    @Transactional
    void deleteResumeFile(Long fileId);
}