package com.RevHire.service.impl;

import com.RevHire.entity.*;
import com.RevHire.repository.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ResumeServiceImplTest {

    @Mock
    private ResumeRepository resumeRepo;

    @Mock
    private ResumeEducationRepository educationRepo;

    @Mock
    private ResumeExperienceRepository experienceRepo;

    @Mock
    private ResumeSkillRepository skillRepo;

    @Mock
    private ResumeFileRepository resumeFileRepo;

    @InjectMocks
    private ResumeServiceImpl resumeService;

    private Resume resume;
    private ResumeEducation education;
    private ResumeExperience experience;
    private ResumeSkill skill;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);

        resume = new Resume();
        resume.setResumeId(1L);
        resume.setObjective("Software Developer");

        education = new ResumeEducation();
        education.setEducationId(1L);
        education.setResume(resume);

        experience = new ResumeExperience();
        experience.setExperienceId(1L);
        experience.setResume(resume);

        skill = new ResumeSkill();
        skill.setSkillId(1L);
        skill.setResume(resume);
    }

    // ================= RESUME =================

    @Test
    void testCreateResume() {

        when(resumeRepo.save(resume)).thenReturn(resume);

        Resume result = resumeService.createResume(resume);

        assertNotNull(result);
        assertEquals("Software Developer", result.getObjective());
    }

    @Test
    void testUpdateResume() {

        Resume updated = new Resume();
        updated.setObjective("Backend Developer");

        when(resumeRepo.findById(1L)).thenReturn(Optional.of(resume));
        when(resumeRepo.save(any(Resume.class))).thenReturn(resume);

        Resume result = resumeService.updateResume(1L, updated);

        assertNotNull(result);
        verify(resumeRepo).save(resume);
    }

    @Test
    void testUpdateResumeNotFound() {

        when(resumeRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> resumeService.updateResume(1L, resume));
    }

    @Test
    void testDeleteResume() {

        resumeService.deleteResume(1L);

        verify(resumeRepo).deleteById(1L);
    }

    // ================= EDUCATION =================

    @Test
    void testAddEducation() {

        when(educationRepo.save(education)).thenReturn(education);

        ResumeEducation result = resumeService.addEducation(education);

        assertNotNull(result);
        verify(educationRepo).save(education);
    }

    @Test
    void testGetEducationByResume() {

        when(educationRepo.findByResume_ResumeId(1L))
                .thenReturn(List.of(education));

        List<ResumeEducation> result =
                resumeService.getEducationByResume(1L);

        assertEquals(1, result.size());
    }

    @Test
    void testDeleteEducation() {

        resumeService.deleteEducation(1L);

        verify(educationRepo).deleteById(1L);
    }

    // ================= EXPERIENCE =================

    @Test
    void testAddExperience() {

        when(experienceRepo.save(experience)).thenReturn(experience);

        ResumeExperience result =
                resumeService.addExperience(experience);

        assertNotNull(result);
        verify(experienceRepo).save(experience);
    }

    @Test
    void testGetExperienceByResume() {

        when(experienceRepo.findByResume_ResumeId(1L))
                .thenReturn(List.of(experience));

        List<ResumeExperience> result =
                resumeService.getExperienceByResume(1L);

        assertEquals(1, result.size());
    }

    @Test
    void testDeleteExperience() {

        resumeService.deleteExperience(1L);

        verify(experienceRepo).deleteById(1L);
    }

    // ================= SKILLS =================

    @Test
    void testAddSkill() {

        when(skillRepo.save(skill)).thenReturn(skill);

        ResumeSkill result = resumeService.addSkill(skill);

        assertNotNull(result);
        verify(skillRepo).save(skill);
    }

    @Test
    void testGetSkillsByResume() {

        when(skillRepo.findByResume_ResumeId(1L))
                .thenReturn(List.of(skill));

        List<ResumeSkill> result =
                resumeService.getSkillsByResume(1L);

        assertEquals(1, result.size());
    }

    @Test
    void testDeleteSkill() {

        resumeService.deleteSkill(1L);

        verify(skillRepo).deleteById(1L);
    }

    @Test
    void testSaveSkills() {

        when(resumeRepo.findById(1L))
                .thenReturn(Optional.of(resume));

        resumeService.saveSkills(1L, List.of(skill));

        verify(skillRepo).deleteByResume(resume);
        verify(skillRepo).saveAll(anyList());
    }
}