package com.RevHire.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ResumeFileDTOTest {

    @Test
    void testSettersAndGetters() {

        ResumeFileDTO dto = new ResumeFileDTO();

        dto.setFileId(1L);
        dto.setResumeId(101L);
        dto.setFileName("resume.pdf");
        dto.setFilePath("/uploads/resume.pdf");
        dto.setFileType("application/pdf");
        dto.setFileSize(2048L);

        assertEquals(1L, dto.getFileId());
        assertEquals(101L, dto.getResumeId());
        assertEquals("resume.pdf", dto.getFileName());
        assertEquals("/uploads/resume.pdf", dto.getFilePath());
        assertEquals("application/pdf", dto.getFileType());
        assertEquals(2048L, dto.getFileSize());
    }

    @Test
    void testObjectCreation() {

        ResumeFileDTO dto = new ResumeFileDTO();

        assertNotNull(dto);
        assertNull(dto.getFileId());
        assertNull(dto.getResumeId());
        assertNull(dto.getFileName());
        assertNull(dto.getFilePath());
        assertNull(dto.getFileType());
        assertNull(dto.getFileSize());
    }
}