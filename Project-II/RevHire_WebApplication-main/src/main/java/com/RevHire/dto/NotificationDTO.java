package com.RevHire.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NotificationDTO {

    private Long notificationId;
    private Long userId;
    private String message;
    private Boolean isRead;

}