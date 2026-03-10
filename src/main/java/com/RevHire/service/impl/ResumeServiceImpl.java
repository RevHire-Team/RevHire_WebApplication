package com.RevHire.service.impl;

import com.RevHire.entity.*;
import com.RevHire.repository.*;
import com.RevHire.service.ResumeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class ResumeServiceImpl implements ResumeService {

    @Autowired
    private ResumeRepository resumeRepo;

    @Autowired
    private ResumeEducationRepository educationRepo;

    @Autowired
    private ResumeExperienceRepository experienceRepo;

    @Autowired
    private ResumeSkillRepository skillRepo;

    @Autowired
    private ResumeCertificationRepository certRepo;

    @Autowired
    private ResumeProjectRepository projectRepo;

    @Autowired
    private ResumeFileRepository fileRepo;

    @Autowired
    private JobSeekerProfileRepository profileRepo;

    /* ================= RESUME ================= */

    @Override
    public Resume createResume(Resume resume) {
        return resumeRepo.save(resume);
    }

    @Override
    public Resume updateResume(Long resumeId, Resume resume) {

        Optional<Resume> existingOpt = resumeRepo.findById(resumeId);

        if (existingOpt.isEmpty()) {
            throw new RuntimeException("Resume not found");
        }

        Resume existing = existingOpt.get();
        existing.setObjective(resume.getObjective());

        return resumeRepo.save(existing);
    }

    @Override
    public void deleteResume(Long resumeId) {
        resumeRepo.deleteById(resumeId);
    }

    @Override
    public Resume getResumeByUserId(Long userId) {

        Optional<JobSeekerProfile> profileOpt =
                profileRepo.findByUserUserId(userId);

        if (profileOpt.isEmpty()) {
            throw new RuntimeException("Profile not found");
        }

        JobSeekerProfile profile = profileOpt.get();

        return resumeRepo.findBySeekerSeekerId(profile.getSeekerId())
                .orElseThrow(() -> new RuntimeException("Resume not found"));
    }

    /* ================= EDUCATION ================= */

    @Override
    public ResumeEducation addEducation(ResumeEducation education) {
        return educationRepo.save(education);
    }

    @Override
    public List<ResumeEducation> getEducationByResume(Long resumeId) {
        return educationRepo.findByResumeResumeId(resumeId);
    }

    @Override
    public void deleteEducation(Long educationId) {
        educationRepo.deleteById(educationId);
    }

    /* ================= EXPERIENCE ================= */

    @Override
    public ResumeExperience addExperience(ResumeExperience experience) {
        return experienceRepo.save(experience);
    }

    @Override
    public List<ResumeExperience> getExperienceByResume(Long resumeId) {
        return experienceRepo.findByResumeResumeId(resumeId);
    }

    @Override
    public void deleteExperience(Long experienceId) {
        experienceRepo.deleteById(experienceId);
    }

    /* ================= SKILLS ================= */

    @Override
    public ResumeSkill addSkill(ResumeSkill skill) {
        return skillRepo.save(skill);
    }

    @Override
    public List<ResumeSkill> getSkillsByResume(Long resumeId) {
        return skillRepo.findByResumeResumeId(resumeId);
    }

    @Override
    public void deleteSkill(Long skillId) {
        skillRepo.deleteById(skillId);
    }

    @Override
    public void saveSkills(Long resumeId, List<ResumeSkill> skills) {

        Resume resume = resumeRepo.findById(resumeId)
                .orElseThrow(() -> new RuntimeException("Resume not found"));

        for (ResumeSkill skill : skills) {
            skill.setResume(resume);
            skillRepo.save(skill);
        }
    }

    /* ================= CERTIFICATIONS ================= */

    @Override
    public ResumeCertification addCertification(Long resumeId, ResumeCertification cert) {

        Resume resume = resumeRepo.findById(resumeId)
                .orElseThrow(() -> new RuntimeException("Resume not found"));

        cert.setResume(resume);

        return certRepo.save(cert);
    }

    @Override
    public List<ResumeCertification> getCertifications(Long resumeId) {
        return certRepo.findByResumeResumeId(resumeId);
    }

    @Override
    public void deleteCertification(Long certificationId) {
        certRepo.deleteById(certificationId);
    }

    /* ================= PROJECTS ================= */

    @Override
    public ResumeProject addProject(Long resumeId, ResumeProject project) {

        Resume resume = resumeRepo.findById(resumeId)
                .orElseThrow(() -> new RuntimeException("Resume not found"));

        project.setResume(resume);

        return projectRepo.save(project);
    }

    @Override
    public List<ResumeProject> getProjects(Long resumeId) {
        return projectRepo.findByResumeResumeId(resumeId);
    }

    @Override
    public void deleteProject(Long projectId) {
        projectRepo.deleteById(projectId);
    }

    /* ================= FILE MANAGEMENT ================= */

    @Override
    public void saveResumeFile(Long userId, MultipartFile file) throws IOException {

        Resume resume = resumeRepo.findBySeeker_User_UserId(userId)
                .orElseThrow(() -> new RuntimeException("Resume not found"));

        String uploadDir = "uploads/";

        Files.createDirectories(Paths.get(uploadDir));

        Path filePath = Paths.get(uploadDir + file.getOriginalFilename());

        Files.write(filePath, file.getBytes());

        ResumeFile resumeFile = new ResumeFile();
        resumeFile.setResume(resume);
        resumeFile.setFileName(file.getOriginalFilename());
        resumeFile.setFilePath(filePath.toString());
        resumeFile.setFileSize(file.getSize());
        resumeFile.setFileType(file.getContentType());

        fileRepo.save(resumeFile);
    }

    @Override
    public ResponseEntity<Resource> downloadResumeFile(Long userId) {

        try {

            Optional<ResumeFile> fileOpt =
                    fileRepo.findByResume_Seeker_User_UserId(userId);

            if (fileOpt.isEmpty()) {
                throw new RuntimeException("Resume file not found");
            }

            ResumeFile file = fileOpt.get();

            Path path = Paths.get(file.getFilePath());

            Resource resource = new UrlResource(path.toUri());

            return ResponseEntity.ok().body(resource);

        } catch (Exception e) {
            throw new RuntimeException("Download failed");
        }
    }

    @Override
    public byte[] getResumeFileBytes(Long userId) {

        try {

            Optional<ResumeFile> fileOpt =
                    fileRepo.findByResume_Seeker_User_UserId(userId);

            if (fileOpt.isEmpty()) {
                throw new RuntimeException("Resume file not found");
            }

            ResumeFile file = fileOpt.get();

            return Files.readAllBytes(Paths.get(file.getFilePath()));

        } catch (Exception e) {
            throw new RuntimeException("File read error");
        }
    }

    @Override
    public ResumeFile getResumeFile(Long fileId) {
        return fileRepo.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found"));
    }

    @Override
    public void deleteResumeFile(Long fileId) {
        fileRepo.deleteByFileId(fileId);
    }
}