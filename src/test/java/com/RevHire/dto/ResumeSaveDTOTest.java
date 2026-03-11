package com.RevHire.dto;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ResumeSaveDTOTest {

    @Test
    void testGettersAndSetters() {
        ResumeSaveDTO resume = new ResumeSaveDTO();

        // Set objective
        resume.setObjective("Seeking a challenging role in software development.");

        // Set educations
        EducationDTO edu = new EducationDTO();
        edu.setInstitution("JNTU");
        edu.setDegree("B.Tech");
        resume.setEducations(List.of(edu));

        // Set experiences
        ExperienceDTO exp = new ExperienceDTO();
        exp.setCompanyName("Rooman Technologies");
        exp.setRole("AI DevOps Engineer");
        resume.setExperiences(List.of(exp));

        // Set projects
        ProjectDTO project = new ProjectDTO();
        project.setProjectTitle("Amazon Clone");
        project.setProjectLink("https://github.com/ameer/amazon-clone");
        project.setDescription("Responsive Amazon homepage clone using Django.");
        resume.setProjects(List.of(project));

        // Set certifications
        CertificationDTO cert = new CertificationDTO();
        cert.setCertificationName("AWS Cloud Practitioner");
        cert.setCompany("Amazon");
        cert.setTechnologies("AWS, Cloud");
        resume.setCertifications(List.of(cert));

        // Set skills
        resume.setSkills(List.of("Java", "Python", "Docker"));

        // Assertions
        assertEquals("Seeking a challenging role in software development.", resume.getObjective());

        assertEquals(1, resume.getEducations().size());
        assertEquals("JNTU", resume.getEducations().get(0).getInstitution());
        assertEquals("B.Tech", resume.getEducations().get(0).getDegree());

        assertEquals(1, resume.getExperiences().size());
        assertEquals("Rooman Technologies", resume.getExperiences().get(0).getCompanyName());
        assertEquals("AI DevOps Engineer", resume.getExperiences().get(0).getRole());

        assertEquals(1, resume.getProjects().size());
        assertEquals("Amazon Clone", resume.getProjects().get(0).getProjectTitle());
        assertEquals("https://github.com/ameer/amazon-clone", resume.getProjects().get(0).getProjectLink());

        assertEquals(1, resume.getCertifications().size());
        assertEquals("AWS Cloud Practitioner", resume.getCertifications().get(0).getCertificationName());

        assertEquals(3, resume.getSkills().size());
        assertTrue(resume.getSkills().contains("Java"));
        assertTrue(resume.getSkills().contains("Python"));
        assertTrue(resume.getSkills().contains("Docker"));
    }
}