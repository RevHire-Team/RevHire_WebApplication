package com.RevHire.dto;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ApplicationDetailsDTOTest {

    @Test
    void testConstructorAndGetters() {
        // Arrange
        Long appId = 1L;
        String name = "John Doe";
        String email = "john@example.com";
        String title = "Java Developer";
        String status = "PENDING";
        LocalDateTime now = LocalDateTime.now();
        Long resId = 101L;
        Long fileId = 202L;

        // Act
        ApplicationDetailsDTO dto = new ApplicationDetailsDTO(
                appId, name, email, title, status, now, resId, fileId
        );

        // Assert
        assertEquals(appId, dto.getApplicationId());
        assertEquals(name, dto.getApplicantName());
        assertEquals(email, dto.getApplicantEmail());
        assertEquals(title, dto.getJobTitle());
        assertEquals(status, dto.getStatus());
        assertEquals(now, dto.getAppliedDate());
        assertEquals(resId, dto.getResumeId());
        assertEquals(fileId, dto.getResumeFileId());
    }

    @Test
    void testSettersAndGetters() {
        // Arrange
        ApplicationDetailsDTO dto = new ApplicationDetailsDTO(
                null, null, null, null, null, null, null, null
        );
        String newStatus = "ACCEPTED";
        Long newFileId = 999L;

        // Act
        dto.setStatus(newStatus);
        dto.setResumeFileId(newFileId);

        // Assert
        assertEquals(newStatus, dto.getStatus());
        assertEquals(newFileId, dto.getResumeFileId());
    }
}