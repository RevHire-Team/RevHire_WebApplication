package com.RevHire.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CertificationDTOTest {

    @Test
    void testGettersAndSetters() {
        CertificationDTO dto = new CertificationDTO();

        // Set values
        dto.setCertificationName("AWS Certified Solutions Architect");
        dto.setCompany("Amazon");
        dto.setTechnologies("AWS, Cloud");

        // Assert getters
        assertEquals("AWS Certified Solutions Architect", dto.getCertificationName());
        assertEquals("Amazon", dto.getCompany());
        assertEquals("AWS, Cloud", dto.getTechnologies());
    }
}