package com.RevHire.service;

//import com.RevHire.dto.NotificationDTO;
import com.RevHire.dto.NotificationDTO;
import com.RevHire.entity.Notification;

import java.util.List;

public interface NotificationService {

    void sendNotification(Long userId, String message);

    List<NotificationDTO> getUserNotifications(Long userId);
}
