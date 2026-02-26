package com.RevHire.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotificationRequestDTO {

    private Long userId;
    private String message;

    public Long getUserId() {
        return userId;
    }

    public String getMessage() {
        return message;
    }
}