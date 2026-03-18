package com.RevHire.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RegisterRequestDTOTest {

    @Test
    void testGettersAndSetters() {
        // Arrange
        RegisterRequestDTO dto = new RegisterRequestDTO();
        String email = "hr@techcorp.com";
        String password = "securePassword123";
        String role = "EMPLOYER";
        String company = "Tech Corp";
        String question = "What is your favorite color?";
        String answer = "Blue";

        // Act
        dto.setEmail(email);
        dto.setPassword(password);
        dto.setRole(role);
        dto.setCompanyName(company);
        dto.setSecurityQuestion(question);
        dto.setSecurityAnswer(answer);

        // Assert
        assertEquals(email, dto.getEmail());
        assertEquals(password, dto.getPassword());
        assertEquals(role, dto.getRole());
        assertEquals(company, dto.getCompanyName());
        assertEquals(question, dto.getSecurityQuestion());
        assertEquals(answer, dto.getSecurityAnswer());
        assertNull(dto.getFullName(), "FullName should be null for an Employer registration");
    }

    @Test
    void testJobSeekerFields() {
        // Arrange
        RegisterRequestDTO dto = new RegisterRequestDTO();
        String name = "Jane Smith";
        String role = "JOB_SEEKER";

        // Act
        dto.setFullName(name);
        dto.setRole(role);

        // Assert
        assertEquals("Jane Smith", dto.getFullName());
        assertEquals("JOB_SEEKER", dto.getRole());
        assertNull(dto.getCompanyName(), "CompanyName should be null for a Job Seeker registration");
    }
}