package com.RevHire.service;

import com.RevHire.entity.Notification;

import java.util.List;

public interface NotificationService {

    void sendNotification(Long userId, String message);

    List<Notification> getUserNotifications(Long userId);
}
