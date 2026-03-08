package com.RevHire.service.impl;

import com.RevHire.dto.NotificationDTO;
import com.RevHire.entity.Notification;
import com.RevHire.entity.User;
import com.RevHire.repository.NotificationRepository;
import com.RevHire.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class NotificationServiceImplTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private User user;
    private Notification notification;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setUserId(1L);
        user.setEmail("user@test.com");

        notification = new Notification();
        notification.setNotificationId(1L);
        notification.setUser(user);
        notification.setMessage("New job posted");
        notification.setIsRead(false);
    }

    // ================= SEND NOTIFICATION =================

    @Test
    void testSendNotificationSuccess() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        notificationService.sendNotification(1L, "New job available");

        verify(notificationRepository).save(any(Notification.class));
    }

    @Test
    void testSendNotificationUserNotFound() {

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> notificationService.sendNotification(1L, "Message"));
    }

    // ================= GET USER NOTIFICATIONS =================

    @Test
    void testGetUserNotifications() {

        when(notificationRepository.findByUserUserId(1L))
                .thenReturn(List.of(notification));

        List<NotificationDTO> result =
                notificationService.getUserNotifications(1L);

        assertEquals(1, result.size());
        assertEquals("New job posted", result.get(0).getMessage());
        assertFalse(result.get(0).getIsRead());
    }

    // ================= MARK AS READ =================

    @Test
    void testMarkAsRead() {

        when(notificationRepository.findById(1L))
                .thenReturn(Optional.of(notification));

        notificationService.markAsRead(1L);

        assertTrue(notification.getIsRead());
        verify(notificationRepository).save(notification);
    }

    @Test
    void testMarkAsReadNotificationNotFound() {

        when(notificationRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> notificationService.markAsRead(1L));
    }
}