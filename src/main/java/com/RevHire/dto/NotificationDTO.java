package com.RevHire.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDTO {

    private Long notificationId;
    private Long userId;
    private String message;
    private Boolean isRead;

}