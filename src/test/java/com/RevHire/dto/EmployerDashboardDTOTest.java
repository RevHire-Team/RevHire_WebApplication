package com.RevHire.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmployerDashboardDTOTest {

    @Test
    void testAllArgsConstructorAndGetters() {

        EmployerDashboardDTO dto = new EmployerDashboardDTO(
                10L,
                5L,
                50L,
                8L,
                75.5
        );

        assertEquals(10L, dto.getTotalJobs());
        assertEquals(5L, dto.getActiveJobs());
        assertEquals(50L, dto.getTotalApplications());
        assertEquals(8L, dto.getPendingReviews());
        assertEquals(75.5, dto.getProfileCompletionPercentage());
    }

    @Test
    void testNoArgsConstructorAndSetters() {

        EmployerDashboardDTO dto = new EmployerDashboardDTO();

        dto.setTotalJobs(20L);
        dto.setActiveJobs(12L);
        dto.setTotalApplications(100L);
        dto.setPendingReviews(15L);
        dto.setProfileCompletionPercentage(90.0);

        assertEquals(20L, dto.getTotalJobs());
        assertEquals(12L, dto.getActiveJobs());
        assertEquals(100L, dto.getTotalApplications());
        assertEquals(15L, dto.getPendingReviews());
        assertEquals(90.0, dto.getProfileCompletionPercentage());
    }

    @Test
    void testToStringMethod() {

        EmployerDashboardDTO dto = new EmployerDashboardDTO(
                10L,
                5L,
                50L,
                8L,
                75.5
        );

        String result = dto.toString();

        assertTrue(result.contains("totalJobs=10"));
        assertTrue(result.contains("activeJobs=5"));
        assertTrue(result.contains("totalApplications=50"));
        assertTrue(result.contains("pendingReviews=8"));
        assertTrue(result.contains("profileCompletionPercentage=75.5"));
    }
}