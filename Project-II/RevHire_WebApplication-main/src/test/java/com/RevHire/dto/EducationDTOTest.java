package com.RevHire.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EducationDTOTest {

    @Test
    void testGettersAndSetters() {
        EducationDTO dto = new EducationDTO();

        // Set values
        dto.setInstitution("JNTU University");
        dto.setDegree("B.Tech Computer Science");

        // Assert getters
        assertEquals("JNTU University", dto.getInstitution());
        assertEquals("B.Tech Computer Science", dto.getDegree());
    }
}