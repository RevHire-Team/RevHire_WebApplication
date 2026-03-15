package com.RevHire.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ApplicationResponseDTOTest {

    @Test
    void testConstructorAndGetters() {
        LocalDateTime appliedDate = LocalDateTime.now();

        ApplicationResponseDTO dto = new ApplicationResponseDTO(
                1L,
                "John Doe",
                "john@example.com",
                "Software Engineer",
                "APPLIED",
                appliedDate
        );

        assertEquals(1L, dto.getId());
        assertEquals("John Doe", dto.getApplicantName());
        assertEquals("john@example.com", dto.getApplicantEmail());
        assertEquals("Software Engineer", dto.getJobTitle());
        assertEquals("APPLIED", dto.getStatus());
        assertEquals(appliedDate, dto.getAppliedDate());
    }

    @Test
    void testSetters() {
        ApplicationResponseDTO dto = new ApplicationResponseDTO(
                1L,
                "John Doe",
                "john@example.com",
                "Software Engineer",
                "APPLIED",
                LocalDateTime.now()
        );

        LocalDateTime newDate = LocalDateTime.now().plusDays(1);

        dto.setId(2L);
        dto.setApplicantName("Jane Doe");
        dto.setApplicantEmail("jane@example.com");
        dto.setJobTitle("Backend Developer");
        dto.setStatus("SHORTLISTED");
        dto.setAppliedDate(newDate);

        assertEquals(2L, dto.getId());
        assertEquals("Jane Doe", dto.getApplicantName());
        assertEquals("jane@example.com", dto.getApplicantEmail());
        assertEquals("Backend Developer", dto.getJobTitle());
        assertEquals("SHORTLISTED", dto.getStatus());
        assertEquals(newDate, dto.getAppliedDate());
    }
}