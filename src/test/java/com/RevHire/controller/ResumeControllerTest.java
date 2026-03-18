package com.RevHire.controller;

import com.RevHire.entity.*;
import com.RevHire.repository.ResumeFileRepository;
import com.RevHire.service.ResumeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ResumeController.class)
class ResumeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ResumeService resumeService;

    @MockBean
    private ResumeFileRepository resumeFileRepo;

    @Autowired
    private ObjectMapper objectMapper;

    private Resume mockResume;

    @BeforeEach
    void setUp() {
        mockResume = new Resume();
        mockResume.setResumeId(1L);
        mockResume.setObjective("Professional Developer");
    }

    // ================= CORE RESUME TESTS =================

    @Test
    void createResume_ShouldReturnSavedResume() throws Exception {
        when(resumeService.createResume(any(Resume.class))).thenReturn(mockResume);

        mockMvc.perform(post("/api/resume/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockResume)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resumeId").value(1))
                .andExpect(jsonPath("$.objective").value("Professional Developer"));
    }

    @Test
    void getResumeDetails_ShouldReturnResume_WhenFound() throws Exception {
        when(resumeService.getResumeByUserId(1L)).thenReturn(mockResume);

        mockMvc.perform(get("/api/resume/details/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resumeId").value(1));
    }

    // ================= EDUCATION TESTS =================

    @Test
    void addEducation_ShouldReturnSavedEducation() throws Exception {
        ResumeEducation edu = new ResumeEducation();
        edu.setInstitution("Revature University");

        when(resumeService.addEducation(any(ResumeEducation.class))).thenReturn(edu);

        mockMvc.perform(post("/api/resume/education/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(edu)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.institution").value("Revature University"));
    }

    // ================= FILE MANAGEMENT TESTS =================

    @Test
    void uploadFile_ShouldReturnSuccessMessage() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.pdf",
                MediaType.APPLICATION_PDF_VALUE, "PDF Content".getBytes());

        // Service returns void, so we just let it execute
        mockMvc.perform(multipart("/api/resume/upload/1").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("File uploaded successfully"));
    }

    @Test
    void deleteResumeFile_ShouldReturnSuccess() throws Exception {
        doNothing().when(resumeService).deleteResumeFile(1L);

        mockMvc.perform(delete("/api/resume/file/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Deleted"));
    }

    // ================= PDF GENERATION TEST =================

    @Test
    void generateResumePdf_ShouldReturnPdfBytes() throws Exception {
        // Setup: No physical files in list to force the fallback PDF generation
        mockResume.setFiles(Collections.emptyList());

        when(resumeService.getResumeById(1L)).thenReturn(mockResume);
        when(resumeService.generateResumeHtml(1L)).thenReturn("<html><body>Resume</body></html>");

        mockMvc.perform(get("/api/resume/resume-pdf/1"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/pdf"))
                .andExpect(content().contentType(MediaType.APPLICATION_PDF));
    }
}