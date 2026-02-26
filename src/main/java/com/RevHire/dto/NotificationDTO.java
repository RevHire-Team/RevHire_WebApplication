package com.RevHire.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotificationDTO {

    private Long notificationId;
    private String message;
    private String isRead;

    public NotificationDTO(Long notificationId, String message, String isRead) {
        this.notificationId = notificationId;
        this.message = message;
        this.isRead = isRead;
    }

    // getters
}