package com.RevHire.controller;

import com.RevHire.dto.NotificationRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.RevHire.service.NotificationService;

import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @PostMapping("/send")
    public ResponseEntity<?> sendNotification(@RequestBody NotificationRequestDTO request) {

        notificationService.sendNotification(
                request.getUserId(),
                request.getMessage()
        );

        return ResponseEntity.ok().body(Map.of(
                "status", "success",
                "message", "Notification sent successfully"
        ));
    }

  @GetMapping("/{userId}")
  public ResponseEntity<?> getNotifications(@PathVariable Long userId) {

            return ResponseEntity.ok(
                    notificationService.getUserNotifications(userId)
            );
        }

}