package com.RevHire.controller;

import com.RevHire.entity.*;
import com.RevHire.repository.ResumeFileRepository;
import com.RevHire.service.ResumeService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ResumeController.class)
public class ResumeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ResumeService resumeService;

    @MockBean
    private ResumeFileRepository resumeFileRepo;

    private ObjectMapper objectMapper;

    private Resume resume;

    @BeforeEach
    void setup() {

        objectMapper = new ObjectMapper();

        resume = new Resume();
        resume.setResumeId(1L);   // only using fields that definitely exist
    }

    // ================= RESUME =================

    @Test
    void testCreateResume() throws Exception {

        Mockito.when(resumeService.createResume(any(Resume.class)))
                .thenReturn(resume);

        mockMvc.perform(post("/api/resume/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(resume)))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateResume() throws Exception {

        Mockito.when(resumeService.updateResume(anyLong(), any(Resume.class)))
                .thenReturn(resume);

        mockMvc.perform(put("/api/resume/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(resume)))
                .andExpect(status().isOk());
    }

    @Test
    void testGetResumeDetails() throws Exception {

        Mockito.when(resumeService.getResumeByUserId(anyLong()))
                .thenReturn(resume);

        mockMvc.perform(get("/api/resume/details/1"))
                .andExpect(status().isOk());
    }

    // ================= EDUCATION =================

    @Test
    void testAddEducation() throws Exception {

        ResumeEducation education = new ResumeEducation();

        Mockito.when(resumeService.addEducation(any(ResumeEducation.class)))
                .thenReturn(education);

        mockMvc.perform(post("/api/resume/education/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(education)))
                .andExpect(status().isOk());
    }

    @Test
    void testGetEducation() throws Exception {

        List<ResumeEducation> list = new ArrayList<>();
        list.add(new ResumeEducation());

        Mockito.when(resumeService.getEducationByResume(anyLong()))
                .thenReturn(list);

        mockMvc.perform(get("/api/resume/education/1"))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteEducation() throws Exception {

        mockMvc.perform(delete("/api/resume/education/delete/1"))
                .andExpect(status().isOk());
    }

    // ================= EXPERIENCE =================

    @Test
    void testAddExperience() throws Exception {

        ResumeExperience experience = new ResumeExperience();

        Mockito.when(resumeService.addExperience(any(ResumeExperience.class)))
                .thenReturn(experience);

        mockMvc.perform(post("/api/resume/experience/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(experience)))
                .andExpect(status().isOk());
    }

    @Test
    void testGetExperience() throws Exception {

        List<ResumeExperience> list = new ArrayList<>();
        list.add(new ResumeExperience());

        Mockito.when(resumeService.getExperienceByResume(anyLong()))
                .thenReturn(list);

        mockMvc.perform(get("/api/resume/experience/1"))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteExperience() throws Exception {

        mockMvc.perform(delete("/api/resume/experience/delete/1"))
                .andExpect(status().isOk());
    }

    // ================= SKILLS =================

    @Test
    void testAddSkill() throws Exception {

        ResumeSkill skill = new ResumeSkill();

        Mockito.when(resumeService.addSkill(any(ResumeSkill.class)))
                .thenReturn(skill);

        mockMvc.perform(post("/api/resume/skill/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(skill)))
                .andExpect(status().isOk());
    }

    @Test
    void testGetSkills() throws Exception {

        List<ResumeSkill> list = new ArrayList<>();
        list.add(new ResumeSkill());

        Mockito.when(resumeService.getSkillsByResume(anyLong()))
                .thenReturn(list);

        mockMvc.perform(get("/api/resume/skill/1"))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteSkill() throws Exception {

        mockMvc.perform(delete("/api/resume/skill/delete/1"))
                .andExpect(status().isOk());
    }

    // ================= FILE UPLOAD =================

    @Test
    void testUploadFile() throws Exception {

        MockMultipartFile file =
                new MockMultipartFile(
                        "file",
                        "resume.pdf",
                        "application/pdf",
                        "dummy content".getBytes());

        mockMvc.perform(multipart("/api/resume/upload/1")
                        .file(file))
                .andExpect(status().isOk());
    }

    // ================= FILE DELETE =================

    @Test
    void testDeleteResumeFile() throws Exception {

        mockMvc.perform(delete("/api/resume/file/1"))
                .andExpect(status().isOk());
    }

}