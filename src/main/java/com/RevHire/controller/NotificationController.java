package com.RevHire.controller;

//import com.RevHire.dto.NotificationRequestDTO;
import com.RevHire.dto.NotificationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.RevHire.service.NotificationService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @PostMapping("/send")
    public ResponseEntity<Map<String, String>> sendNotification(@RequestBody NotificationDTO request) {
        notificationService.sendNotification(request.getUserId(), request.getMessage());

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Notification sent successfully"
        ));
    }

    @GetMapping("/{userId}")
    public List<NotificationDTO> getNotifications(@PathVariable Long userId) {
        return notificationService.getUserNotifications(userId);
    }

}