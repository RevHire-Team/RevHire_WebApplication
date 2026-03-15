package com.RevHire.dto;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class FavoriteJobDTOTest {

    @Test
    void testAllArgsConstructorAndGetters() {

        FavoriteJobDTO dto = new FavoriteJobDTO(
                1L,
                101L,
                "Java Developer",
                "Bangalore",
                new BigDecimal("50000"),
                new BigDecimal("80000"),
                "Full-Time",
                "OPEN",
                "Tech Corp"
        );

        assertEquals(1L, dto.getFavId());
        assertEquals(101L, dto.getJobId());
        assertEquals("Java Developer", dto.getTitle());
        assertEquals("Bangalore", dto.getLocation());
        assertEquals(new BigDecimal("50000"), dto.getSalaryMin());
        assertEquals(new BigDecimal("80000"), dto.getSalaryMax());
        assertEquals("Full-Time", dto.getJobType());
        assertEquals("OPEN", dto.getStatus());
        assertEquals("Tech Corp", dto.getCompanyName());
    }

    @Test
    void testSetters() {

        FavoriteJobDTO dto = new FavoriteJobDTO(
                1L,
                101L,
                "Java Developer",
                "Bangalore",
                new BigDecimal("50000"),
                new BigDecimal("80000"),
                "Full-Time",
                "OPEN",
                "Tech Corp"
        );

        dto.setFavId(2L);
        dto.setJobId(202L);
        dto.setTitle("Backend Developer");
        dto.setLocation("Hyderabad");
        dto.setSalaryMin(new BigDecimal("60000"));
        dto.setSalaryMax(new BigDecimal("90000"));
        dto.setJobType("Contract");
        dto.setStatus("CLOSED");
        dto.setCompanyName("ABC Pvt Ltd");

        assertEquals(2L, dto.getFavId());
        assertEquals(202L, dto.getJobId());
        assertEquals("Backend Developer", dto.getTitle());
        assertEquals("Hyderabad", dto.getLocation());
        assertEquals(new BigDecimal("60000"), dto.getSalaryMin());
        assertEquals(new BigDecimal("90000"), dto.getSalaryMax());
        assertEquals("Contract", dto.getJobType());
        assertEquals("CLOSED", dto.getStatus());
        assertEquals("ABC Pvt Ltd", dto.getCompanyName());
    }

    @Test
    void testEqualsHashCodeAndToString() {

        FavoriteJobDTO dto1 = new FavoriteJobDTO(
                1L, 101L, "Java Developer", "Bangalore",
                new BigDecimal("50000"), new BigDecimal("80000"),
                "Full-Time", "OPEN", "Tech Corp"
        );

        FavoriteJobDTO dto2 = new FavoriteJobDTO(
                1L, 101L, "Java Developer", "Bangalore",
                new BigDecimal("50000"), new BigDecimal("80000"),
                "Full-Time", "OPEN", "Tech Corp"
        );

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());

        String result = dto1.toString();
        assertTrue(result.contains("Java Developer"));
        assertTrue(result.contains("Bangalore"));
        assertTrue(result.contains("Tech Corp"));
    }
}