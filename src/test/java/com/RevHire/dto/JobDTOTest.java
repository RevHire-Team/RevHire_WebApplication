package com.RevHire.dto;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class JobDTOTest {

    @Test
    void testConstructorAndGetters() {

        JobDTO jobDTO = new JobDTO(
                1L,
                "Java Developer",
                "Bangalore",
                new BigDecimal("50000"),
                new BigDecimal("80000"),
                "Full-Time",
                "OPEN",
                "Tech Corp"
        );

        assertEquals(1L, jobDTO.getJobId());
        assertEquals("Java Developer", jobDTO.getTitle());
        assertEquals("Bangalore", jobDTO.getLocation());
        assertEquals(new BigDecimal("50000"), jobDTO.getSalaryMin());
        assertEquals(new BigDecimal("80000"), jobDTO.getSalaryMax());
        assertEquals("Full-Time", jobDTO.getJobType());
        assertEquals("OPEN", jobDTO.getStatus());
        assertEquals("Tech Corp", jobDTO.getCompanyName());
    }

    @Test
    void testSettersAndGetters() {

        JobDTO jobDTO = new JobDTO(
                1L,
                "Java Developer",
                "Bangalore",
                new BigDecimal("50000"),
                new BigDecimal("80000"),
                "Full-Time",
                "OPEN",
                "Tech Corp"
        );

        jobDTO.setJobId(2L);
        jobDTO.setTitle("Backend Developer");
        jobDTO.setLocation("Hyderabad");
        jobDTO.setSalaryMin(new BigDecimal("60000"));
        jobDTO.setSalaryMax(new BigDecimal("90000"));
        jobDTO.setJobType("Contract");
        jobDTO.setStatus("CLOSED");
        jobDTO.setCompanyName("ABC Pvt Ltd");

        assertEquals(2L, jobDTO.getJobId());
        assertEquals("Backend Developer", jobDTO.getTitle());
        assertEquals("Hyderabad", jobDTO.getLocation());
        assertEquals(new BigDecimal("60000"), jobDTO.getSalaryMin());
        assertEquals(new BigDecimal("90000"), jobDTO.getSalaryMax());
        assertEquals("Contract", jobDTO.getJobType());
        assertEquals("CLOSED", jobDTO.getStatus());
        assertEquals("ABC Pvt Ltd", jobDTO.getCompanyName());
    }

    @Test
    void testEqualsHashCodeAndToString() {

        JobDTO job1 = new JobDTO(
                1L, "Java Developer", "Bangalore",
                new BigDecimal("50000"), new BigDecimal("80000"),
                "Full-Time", "OPEN", "Tech Corp"
        );

        JobDTO job2 = new JobDTO(
                1L, "Java Developer", "Bangalore",
                new BigDecimal("50000"), new BigDecimal("80000"),
                "Full-Time", "OPEN", "Tech Corp"
        );

        assertEquals(job1, job2);
        assertEquals(job1.hashCode(), job2.hashCode());

        String result = job1.toString();

        assertTrue(result.contains("Java Developer"));
        assertTrue(result.contains("Bangalore"));
        assertTrue(result.contains("Tech Corp"));
    }
}