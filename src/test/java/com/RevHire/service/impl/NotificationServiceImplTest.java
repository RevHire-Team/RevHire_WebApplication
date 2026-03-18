package com.RevHire.service.impl;

import com.RevHire.dto.NotificationDTO;
import com.RevHire.entity.Notification;
import com.RevHire.entity.User;
import com.RevHire.repository.NotificationRepository;
import com.RevHire.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // cleaner than MockitoAnnotations.openMocks
class NotificationServiceImplTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    @Captor
    private ArgumentCaptor<Notification> notificationCaptor;

    private User user;
    private Notification notification;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUserId(1L);
        user.setEmail("user@test.com");

        notification = new Notification();
        notification.setNotificationId(10L);
        notification.setUser(user);
        notification.setMessage("Test Message");
        notification.setIsRead(false);
    }

    // ================= SEND NOTIFICATION =================

    @Test
    void testSendNotification_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        notificationService.sendNotification(1L, "New Alert");

        // Use captor to verify internal state of the notification created inside the method
        verify(notificationRepository).save(notificationCaptor.capture());
        Notification saved = notificationCaptor.getValue();

        assertEquals("New Alert", saved.getMessage());
        assertEquals(user, saved.getUser());
        assertFalse(saved.getIsRead());
    }

    @Test
    void testSendNotification_UserNotFound_ChecksMessage() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class,
                () -> notificationService.sendNotification(1L, "Message"));

        assertTrue(exception.getMessage().contains("User not found with ID: 1"));
    }

    // ================= GET USER NOTIFICATIONS =================

    @Test
    void testGetUserNotifications_ReturnsData() {
        when(notificationRepository.findByUserUserId(1L)).thenReturn(List.of(notification));

        List<NotificationDTO> result = notificationService.getUserNotifications(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(10L, result.get(0).getNotificationId());
        verify(notificationRepository, times(1)).findByUserUserId(1L);
    }

    @Test
    void testGetUserNotifications_EmptyList() {
        // Covers the stream/mapping logic for an empty collection
        when(notificationRepository.findByUserUserId(1L)).thenReturn(Collections.emptyList());

        List<NotificationDTO> result = notificationService.getUserNotifications(1L);

        assertTrue(result.isEmpty());
    }

    // ================= MARK AS READ =================

    @Test
    void testMarkAsRead_Success() {
        when(notificationRepository.findById(10L)).thenReturn(Optional.of(notification));

        notificationService.markAsRead(10L);

        // Verify the status was actually flipped
        assertTrue(notification.getIsRead());
        verify(notificationRepository).save(notification);
    }

    @Test
    void testMarkAsRead_NotFound_ChecksMessage() {
        when(notificationRepository.findById(10L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class,
                () -> notificationService.markAsRead(10L));

        assertEquals("Notif not found", exception.getMessage());
        verify(notificationRepository, never()).save(any());
    }
}