package com.RevHire.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmployerProfileDTOTest {

    @Test
    void testSettersAndGetters() {

        EmployerProfileDTO dto = new EmployerProfileDTO();

        dto.setCompanyName("Tech Solutions");
        dto.setIndustry("Information Technology");
        dto.setCompanySize(200);
        dto.setDescription("Leading software development company");
        dto.setContactEmail("hr@techsolutions.com");
        dto.setWebsite("www.techsolutions.com");
        dto.setLocation("Bangalore");

        assertEquals("Tech Solutions", dto.getCompanyName());
        assertEquals("Information Technology", dto.getIndustry());
        assertEquals(200, dto.getCompanySize());
        assertEquals("Leading software development company", dto.getDescription());
        assertEquals("hr@techsolutions.com", dto.getContactEmail());
        assertEquals("www.techsolutions.com", dto.getWebsite());
        assertEquals("Bangalore", dto.getLocation());
    }

    @Test
    void testEqualsAndHashCode() {

        EmployerProfileDTO dto1 = new EmployerProfileDTO();
        dto1.setCompanyName("Tech Solutions");
        dto1.setIndustry("IT");
        dto1.setCompanySize(100);
        dto1.setDescription("Software Company");
        dto1.setContactEmail("contact@tech.com");
        dto1.setWebsite("www.tech.com");
        dto1.setLocation("Bangalore");

        EmployerProfileDTO dto2 = new EmployerProfileDTO();
        dto2.setCompanyName("Tech Solutions");
        dto2.setIndustry("IT");
        dto2.setCompanySize(100);
        dto2.setDescription("Software Company");
        dto2.setContactEmail("contact@tech.com");
        dto2.setWebsite("www.tech.com");
        dto2.setLocation("Bangalore");

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void testToString() {

        EmployerProfileDTO dto = new EmployerProfileDTO();
        dto.setCompanyName("Tech Solutions");
        dto.setIndustry("IT");
        dto.setCompanySize(100);
        dto.setDescription("Software Company");
        dto.setContactEmail("contact@tech.com");
        dto.setWebsite("www.tech.com");
        dto.setLocation("Bangalore");

        String result = dto.toString();

        assertTrue(result.contains("Tech Solutions"));
        assertTrue(result.contains("IT"));
        assertTrue(result.contains("Bangalore"));
    }
}