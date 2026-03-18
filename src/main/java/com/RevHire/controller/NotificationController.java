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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Controller
@RequestMapping("/notifications")
public class NotificationController {

    private static final Logger logger = LogManager.getLogger(NotificationController.class);

    @Autowired
    private NotificationService notificationService;

    // Serves the HTML Page
    @GetMapping("/notification")
    public String showNotificationsPage(HttpSession session) {

        logger.info("Opening notifications page");

        if (session.getAttribute("userId") == null) {
            logger.warn("Unauthorized attempt to access notifications page");
            return "redirect:/auth/login";
        }

        return "notifications";
    }

    //  Serves JSON data for the list
    @GetMapping("/api/{userId}")
    @ResponseBody
    public List<NotificationDTO> getNotifications(@PathVariable Long userId) {

        logger.info("Fetching notifications for userId {}", userId);

        return notificationService.getUserNotifications(userId);
    }

    //Marks a single notification as read
    @PutMapping("/api/{id}/read")
    @ResponseBody
    public ResponseEntity<?> markRead(@PathVariable Long id) {

        logger.info("Marking notification {} as read", id);

        notificationService.markAsRead(id);

        return ResponseEntity.ok().build();
    }
}