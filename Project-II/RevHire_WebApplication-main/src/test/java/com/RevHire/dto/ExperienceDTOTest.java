package com.RevHire.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ExperienceDTOTest {

    @Test
    void testGettersAndSetters() {
        ExperienceDTO dto = new ExperienceDTO();

        // Set values
        dto.setCompanyName("Rooman Technologies Pvt. Ltd.");
        dto.setRole("AI DevOps Engineer");

        // Assert getters
        assertEquals("Rooman Technologies Pvt. Ltd.", dto.getCompanyName());
        assertEquals("AI DevOps Engineer", dto.getRole());
    }
}