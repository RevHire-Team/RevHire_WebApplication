package com.RevHire.service.impl;

import com.RevHire.entity.*;
import com.RevHire.repository.*;
import com.RevHire.service.ResumeService;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.List;
import java.time.LocalDateTime;

    @Service
    public class ResumeServiceImpl implements ResumeService {

        @Autowired
        private final ResumeRepository resumeRepo;
        private final ResumeEducationRepository educationRepo;
        private final ResumeExperienceRepository experienceRepo;
        private final ResumeSkillRepository skillRepo;
        private final ResumeFileRepository resumeFileRepo; // Add this repository

        public ResumeServiceImpl(ResumeRepository resumeRepo,
                                 ResumeEducationRepository educationRepo,
                                 ResumeExperienceRepository experienceRepo,
                                 ResumeSkillRepository skillRepo, ResumeFileRepository resumeFileRepo) {
            this.resumeRepo = resumeRepo;
            this.educationRepo = educationRepo;
            this.experienceRepo = experienceRepo;
            this.skillRepo = skillRepo;
            this.resumeFileRepo = resumeFileRepo;
        }

        // ================= RESUME =================

        @Override
        public Resume createResume(Resume resume) {
            return resumeRepo.save(resume);
        }

        @Override
        public Resume updateResume(Long resumeId, Resume updatedResume) {
            Resume existing = resumeRepo.findById(resumeId)
                    .orElseThrow(() -> new RuntimeException("Resume not found"));

            existing.setObjective(updatedResume.getObjective());
            return resumeRepo.save(existing);
        }

        @Override
        public void deleteResume(Long resumeId) {
            resumeRepo.deleteById(resumeId);
        }

        @Override
        public Resume getResumeByUserId(Long seekerId) {
            return resumeRepo.findBySeeker_SeekerId(seekerId)
                    .orElseThrow(() -> new RuntimeException("Resume not found for Seeker ID: " + seekerId));
        }

        // ================= EDUCATION =================

        @Override
        public ResumeEducation addEducation(ResumeEducation education) {
            return educationRepo.save(education);
        }

        @Override
        public List<ResumeEducation> getEducationByResume(Long resumeId) {
            return educationRepo.findByResume_ResumeId(resumeId);
        }

        @Override
        public void deleteEducation(Long educationId) {
            educationRepo.deleteById(educationId);
        }

        // ================= EXPERIENCE =================

        @Override
        public ResumeExperience addExperience(ResumeExperience experience) {
            return experienceRepo.save(experience);
        }

        @Override
        public List<ResumeExperience> getExperienceByResume(Long resumeId) {
            return experienceRepo.findByResume_ResumeId(resumeId);
        }

        @Override
        public void deleteExperience(Long experienceId) {
            experienceRepo.deleteById(experienceId);
        }

        // ================= SKILLS =================

        @Override
        public ResumeSkill addSkill(ResumeSkill skill) {
            return skillRepo.save(skill);
        }

        @Override
        public List<ResumeSkill> getSkillsByResume(Long resumeId) {
            return skillRepo.findByResume_ResumeId(resumeId);
        }

        @Override
        public void deleteSkill(Long skillId) {
            skillRepo.deleteById(skillId);
        }

        @Override
        @Transactional
        public void saveSkills(Long resumeId, List<ResumeSkill> skills) {
            Resume resume = resumeRepo.findById(resumeId)
                    .orElseThrow(() -> new RuntimeException("Resume not found"));

            skillRepo.deleteByResume(resume);

            for (ResumeSkill skill : skills) {
                skill.setResume(resume);
            }

            skillRepo.saveAll(skills);
        }

        // ================= FILE UPLOAD =================

        // ================= FILE UPLOAD & MANAGEMENT =================

        @Override
        @Transactional
        public void saveResumeFile(Long userId, MultipartFile file) throws IOException {
            Resume resume = resumeRepo.findBySeeker_User_UserId(userId)
                    .orElseThrow(() -> new RuntimeException("Resume profile not found"));

            // 1. Create the directory if it doesn't exist
            Path uploadPath = Paths.get("uploads");
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 2. Create unique filename to avoid overwriting
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);

            // 3. PHYSICALLY save the file to the disk
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // 4. Save metadata to DB
            ResumeFile resumeFile = new ResumeFile();
            resumeFile.setFileName(file.getOriginalFilename());
            resumeFile.setFileSize(file.getSize());
            resumeFile.setResume(resume);
            resumeFile.setUploadedAt(LocalDateTime.now());
            resumeFile.setFilePath(filePath.toString()); // Store the full relative path

            resumeFile.setFileType(file.getContentType().contains("pdf") ? "PDF" : "DOCX");

            resumeFileRepo.save(resumeFile);
        }

        @Override
        @Transactional
        public void deleteResumeFile(Long fileId) {
            if (resumeFileRepo.existsById(fileId)) {
                resumeFileRepo.deleteById(fileId);
                // Force the DB to commit the change immediately
                // before the frontend calls loadResumes() again
                resumeFileRepo.flush();
            }
        }

        @Override
        public byte[] getResumeFileBytes(Long seekerId) {
            Resume resume = resumeRepo.findBySeeker_SeekerId(seekerId)
                    .orElseThrow(() -> new RuntimeException("No resume found"));

            ResumeFile latestFile = resume.getFiles().stream()
                    .max(Comparator.comparing(ResumeFile::getFileId))
                    .orElseThrow(() -> new RuntimeException("No resume file uploaded"));

            try {
                return Files.readAllBytes(Paths.get(latestFile.getFilePath()));
            } catch (IOException e) {
                throw new RuntimeException("Unable to read resume file");
            }
        }

        public ResponseEntity<jakarta.annotation.Resource> downloadResumeFile(Long userId) {

            Resume resume = resumeRepo.findBySeekerSeekerId(userId)
                    .orElseThrow(() -> new RuntimeException("No resume found"));

            ResumeFile latestFile = resume.getFiles().stream()
                    .max(Comparator.comparing(ResumeFile::getFileId))
                    .orElseThrow(() -> new RuntimeException("No file metadata found"));

            try {
                Path filePath = Paths.get(latestFile.getFilePath());

                ByteArrayResource resource =
                        new ByteArrayResource(Files.readAllBytes(filePath));

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(latestFile.getFileType()))
                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                "attachment; filename=\"" + latestFile.getFileName() + "\"")
                        .body((jakarta.annotation.Resource) resource);

            } catch (IOException e) {
                throw new RuntimeException("Could not download file");
            }
        }

        @Transactional
        @Override
        public void saveResumeDetails(Long userId, Resume resumeData) {
            // 1. Find the existing resume record for this user
            Resume existingResume = resumeRepo.findBySeeker_User_UserId(userId)
                    .orElseThrow(() -> new RuntimeException("Resume profile not found"));

            // 2. Update Objective
            existingResume.setObjective(resumeData.getObjective());

            // 3. Update Education: Clear old and add new
            educationRepo.deleteByResume(existingResume);
            if (resumeData.getEducations() != null) {
                resumeData.getEducations().forEach(edu -> {
                    edu.setResume(existingResume);
                    educationRepo.save(edu);
                });
            }

            // 4. Update Experience: Clear old and add new
            experienceRepo.deleteByResume(existingResume);
            if (resumeData.getExperiences() != null) {
                resumeData.getExperiences().forEach(exp -> {
                    exp.setResume(existingResume);
                    experienceRepo.save(exp);
                });
            }

            // 5. Update Skills: Clear old and add new
            skillRepo.deleteByResume(existingResume);
            if (resumeData.getSkills() != null) {
                resumeData.getSkills().forEach(skill -> {
                    skill.setResume(existingResume);
                    skillRepo.save(skill);
                });
            }

            resumeRepo.save(existingResume);
        }
    }