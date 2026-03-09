package com.RevHire.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class EmployerApplicationDTOTest {

    @Test
    void testAllArgsConstructorAndGetters() {

        LocalDateTime appliedDate = LocalDateTime.now();

        EmployerApplicationDTO dto = new EmployerApplicationDTO(
                1L,
                "Software Engineer",
                101L,
                "John Doe",
                "john@example.com",
                "APPLIED",
                appliedDate,
                501L
        );

        assertEquals(1L, dto.getApplicationId());
        assertEquals("Software Engineer", dto.getJobTitle());
        assertEquals(101L, dto.getJobId());
        assertEquals("John Doe", dto.getApplicantName());
        assertEquals("john@example.com", dto.getApplicantEmail());
        assertEquals("APPLIED", dto.getStatus());
        assertEquals(appliedDate, dto.getAppliedDate());
        assertEquals(501L, dto.getResumeId());
    }

    @Test
    void testNoArgsConstructor() {

        EmployerApplicationDTO dto = new EmployerApplicationDTO();

        assertNotNull(dto);
        assertNull(dto.getApplicationId());
        assertNull(dto.getJobTitle());
        assertNull(dto.getJobId());
        assertNull(dto.getApplicantName());
        assertNull(dto.getApplicantEmail());
        assertNull(dto.getStatus());
        assertNull(dto.getAppliedDate());
        assertNull(dto.getResumeId());
    }
}