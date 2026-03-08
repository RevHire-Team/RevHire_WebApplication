package com.RevHire.controller;

import com.RevHire.dto.NotificationDTO;
import com.RevHire.service.NotificationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    // 1. Serves the HTML Page
    @GetMapping("/notification")
    public String showNotificationsPage(HttpSession session) {
        if (session.getAttribute("userId") == null) return "redirect:/auth/login";
        return "notifications";
    }

    // 2. Serves JSON data for the list
    @GetMapping("/api/{userId}")
    @ResponseBody
    public List<NotificationDTO> getNotifications(@PathVariable Long userId) {
        return notificationService.getUserNotifications(userId);
    }

    // 3. New: API for the Dashboard Badge Count
    @GetMapping("/api/unread-count/{userId}")
    @ResponseBody
    public ResponseEntity<Map<String, Long>> getUnreadCount(@PathVariable Long userId) {
        long count = notificationService.getUserNotifications(userId)
                .stream()
                .filter(n -> !n.getIsRead())
                .count();
        return ResponseEntity.ok(Map.of("count", count));
    }

    // 4. Marks a single notification as read
    // Marks a single notification as read
// Access this via: /notifications/api/5/read
    @PutMapping("/api/{id}/read")
    @ResponseBody
    public ResponseEntity<?> markRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }
}