package com.RevHire.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ProjectDTOTest {

    @Test
    void testGettersAndSetters() {
        ProjectDTO dto = new ProjectDTO();

        // Set values
        dto.setProjectTitle("Amazon Homepage Clone");
        dto.setProjectLink("https://github.com/ameer/amazon-clone");
        dto.setDescription("A responsive Amazon homepage clone using Django, HTML, and CSS.");

        // Assert getters
        assertEquals("Amazon Homepage Clone", dto.getProjectTitle());
        assertEquals("https://github.com/ameer/amazon-clone", dto.getProjectLink());
        assertEquals("A responsive Amazon homepage clone using Django, HTML, and CSS.", dto.getDescription());
    }
}