package com.RevHire.service;

import com.RevHire.entity.*;
import com.RevHire.repository.*;
import com.RevHire.service.impl.ResumeServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ResumeServiceTest {

    @Mock
    private ResumeRepository resumeRepository;

    @Mock
    private ResumeEducationRepository educationRepository;

    @Mock
    private ResumeExperienceRepository experienceRepository;

    @Mock
    private ResumeSkillRepository skillRepository;

    @InjectMocks
    private ResumeServiceImpl resumeService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    // -------- Resume --------

    @Test
    void testCreateResume() {

        Resume resume = new Resume();
        resume.setObjective("Java Developer");

        when(resumeRepository.save(resume)).thenReturn(resume);

        Resume result = resumeService.createResume(resume);

        assertNotNull(result);
        verify(resumeRepository).save(resume);
    }

    @Test
    void testUpdateResume() {

        Resume resume = new Resume();
        resume.setResumeId(1L);
        resume.setObjective("Old");

        Resume updated = new Resume();
        updated.setObjective("New Objective");

        when(resumeRepository.findById(1L))
                .thenReturn(Optional.of(resume));

        when(resumeRepository.save(any())).thenReturn(resume);

        Resume result = resumeService.updateResume(1L, updated);

        assertEquals("New Objective", result.getObjective());
    }

    @Test
    void testDeleteResume() {

        resumeService.deleteResume(1L);

        verify(resumeRepository).deleteById(1L);
    }

    // -------- Education --------

    @Test
    void testAddEducation() {

        ResumeEducation education = new ResumeEducation();
        education.setDegree("B.Tech");

        when(educationRepository.save(education)).thenReturn(education);

        ResumeEducation result = resumeService.addEducation(education);

        assertNotNull(result);
    }

    @Test
    void testGetEducationByResume() {

        ResumeEducation education = new ResumeEducation();
        education.setDegree("B.Tech");

        when(educationRepository.findByResume_ResumeId(1L))
                .thenReturn(List.of(education));

        List<ResumeEducation> result =
                resumeService.getEducationByResume(1L);

        assertEquals(1,result.size());
    }

    // -------- Experience --------

    @Test
    void testAddExperience() {

        ResumeExperience experience = new ResumeExperience();
        experience.setCompanyName("TCS");

        when(experienceRepository.save(experience))
                .thenReturn(experience);

        ResumeExperience result =
                resumeService.addExperience(experience);

        assertNotNull(result);
    }

    @Test
    void testGetExperienceByResume() {

        ResumeExperience experience = new ResumeExperience();
        experience.setCompanyName("Infosys");

        when(experienceRepository.findByResume_ResumeId(1L))
                .thenReturn(List.of(experience));

        List<ResumeExperience> result =
                resumeService.getExperienceByResume(1L);

        assertEquals(1,result.size());
    }

    // -------- Skills --------

    @Test
    void testAddSkill() {

        ResumeSkill skill = new ResumeSkill();
        skill.setSkillName("Java");

        when(skillRepository.save(skill)).thenReturn(skill);

        ResumeSkill result = resumeService.addSkill(skill);

        assertNotNull(result);
    }

    @Test
    void testGetSkillsByResume() {

        ResumeSkill skill = new ResumeSkill();
        skill.setSkillName("Spring Boot");

        when(skillRepository.findByResume_ResumeId(1L))
                .thenReturn(List.of(skill));

        List<ResumeSkill> result =
                resumeService.getSkillsByResume(1L);

        assertEquals(1,result.size());
    }
}