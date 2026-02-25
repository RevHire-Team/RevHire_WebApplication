package com.RevHire.service.impl;

import com.RevHire.entity.*;
import com.RevHire.repository.*;
import com.RevHire.service.ResumeService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResumeServiceImpl implements ResumeService {

    private final ResumeRepository resumeRepo;
    private final ResumeEducationRepository educationRepo;
    private final ResumeExperienceRepository experienceRepo;
    private final ResumeSkillRepository skillRepo;

    public ResumeServiceImpl(ResumeRepository resumeRepo,
                             ResumeEducationRepository educationRepo,
                             ResumeExperienceRepository experienceRepo,
                             ResumeSkillRepository skillRepo) {
        this.resumeRepo = resumeRepo;
        this.educationRepo = educationRepo;
        this.experienceRepo = experienceRepo;
        this.skillRepo = skillRepo;
    }

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
}