package com.RevHire.service;

import com.RevHire.dto.NotificationDTO;
import com.RevHire.entity.Notification;
import com.RevHire.entity.User;
import com.RevHire.repository.NotificationRepository;
import com.RevHire.repository.UserRepository;
import com.RevHire.service.impl.NotificationServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSendNotification() {

        User user = new User();
        user.setUserId(1L);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        notificationService.sendNotification(1L,"New Job Posted");

        verify(notificationRepository,times(1))
                .save(any(Notification.class));
    }

    @Test
    void testGetUserNotifications() {

        User user = new User();
        user.setUserId(1L);

        Notification notification = new Notification();
        notification.setNotificationId(10L);
        notification.setUser(user);
        notification.setMessage("Application Accepted");
        notification.setIsRead(false);

        when(notificationRepository.findByUserUserId(1L))
                .thenReturn(List.of(notification));

        List<NotificationDTO> result =
                notificationService.getUserNotifications(1L);

        assertEquals(1,result.size());
        assertEquals("Application Accepted",result.get(0).getMessage());
    }
}