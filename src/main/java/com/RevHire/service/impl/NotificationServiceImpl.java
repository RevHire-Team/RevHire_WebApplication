package com.RevHire.service.impl;

import com.RevHire.dto.NotificationDTO;
import com.RevHire.entity.Notification;
import com.RevHire.repository.NotificationRepository;
import com.RevHire.repository.UserRepository;
import com.RevHire.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public void sendNotification(Long userId, String message) {

        Notification notification = new Notification();
        notification.setUser(userRepository.findById(userId).orElseThrow());
        notification.setMessage(message);
        notification.setIsRead(Boolean.valueOf("N"));

        notificationRepository.save(notification);
    }

    @Override
    public List<NotificationDTO> getUserNotifications(Long userId) {

        List<NotificationDTO> notifications = notificationRepository.findByUserUserId(userId);

        return notifications.stream()
                .map(n -> new NotificationDTO(
                        n.getNotificationId(),
                        n.getMessage(),
                        n.getIsRead()
                ))
                .toList();
    }
}