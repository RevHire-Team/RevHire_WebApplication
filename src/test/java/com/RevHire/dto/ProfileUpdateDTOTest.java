package com.RevHire.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProfileUpdateDTOTest {

    @Test
    void testSettersAndGetters() {

        ProfileUpdateDTO dto = new ProfileUpdateDTO();

        dto.setFullName("John Doe");
        dto.setPhone("9876543210");
        dto.setLocation("Bangalore");
        dto.setJobTitle("Java Developer");
        dto.setSkills("Java, Spring Boot, SQL");
        dto.setEducation("B.Tech Computer Science");
        dto.setExperienceSummary("3 years experience in backend development");

        assertEquals("John Doe", dto.getFullName());
        assertEquals("9876543210", dto.getPhone());
        assertEquals("Bangalore", dto.getLocation());
        assertEquals("Java Developer", dto.getJobTitle());
        assertEquals("Java, Spring Boot, SQL", dto.getSkills());
        assertEquals("B.Tech Computer Science", dto.getEducation());
        assertEquals("3 years experience in backend development", dto.getExperienceSummary());
    }

    @Test
    void testEqualsAndHashCode() {

        ProfileUpdateDTO dto1 = new ProfileUpdateDTO();
        dto1.setFullName("John Doe");
        dto1.setPhone("9876543210");
        dto1.setLocation("Bangalore");
        dto1.setJobTitle("Java Developer");
        dto1.setSkills("Java, Spring Boot, SQL");
        dto1.setEducation("B.Tech Computer Science");
        dto1.setExperienceSummary("3 years experience");

        ProfileUpdateDTO dto2 = new ProfileUpdateDTO();
        dto2.setFullName("John Doe");
        dto2.setPhone("9876543210");
        dto2.setLocation("Bangalore");
        dto2.setJobTitle("Java Developer");
        dto2.setSkills("Java, Spring Boot, SQL");
        dto2.setEducation("B.Tech Computer Science");
        dto2.setExperienceSummary("3 years experience");

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void testToString() {

        ProfileUpdateDTO dto = new ProfileUpdateDTO();
        dto.setFullName("John Doe");
        dto.setLocation("Bangalore");

        String result = dto.toString();

        assertTrue(result.contains("John Doe"));
        assertTrue(result.contains("Bangalore"));
    }
}