package com.RevHire.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NotificationDTOTest {

    @Test
    void testConstructorAndGetters() {

        NotificationDTO dto = new NotificationDTO(
                1L,
                100L,
                "New job application received",
                false
        );

        assertEquals(1L, dto.getNotificationId());
        assertEquals(100L, dto.getUserId());
        assertEquals("New job application received", dto.getMessage());
        assertFalse(dto.getIsRead());
    }

    @Test
    void testSetters() {

        NotificationDTO dto = new NotificationDTO(
                1L,
                100L,
                "New job application received",
                false
        );

        dto.setNotificationId(2L);
        dto.setUserId(200L);
        dto.setMessage("Application status updated");
        dto.setIsRead(true);

        assertEquals(2L, dto.getNotificationId());
        assertEquals(200L, dto.getUserId());
        assertEquals("Application status updated", dto.getMessage());
        assertTrue(dto.getIsRead());
    }

    @Test
    void testEqualsHashCodeAndToString() {

        NotificationDTO dto1 = new NotificationDTO(
                1L, 100L, "Message", false
        );

        NotificationDTO dto2 = new NotificationDTO(
                1L, 100L, "Message", false
        );

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());

        String result = dto1.toString();

        assertTrue(result.contains("notificationId=1"));
        assertTrue(result.contains("userId=100"));
        assertTrue(result.contains("Message"));
    }
}