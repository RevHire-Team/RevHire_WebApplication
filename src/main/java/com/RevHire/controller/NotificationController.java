package com.RevHire.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.RevHire.service.NotificationService;

@Controller
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/{userId}")
    public String getNotifications(@PathVariable Long userId, Model model) {

        model.addAttribute("notifications", notificationService.getUserNotifications(userId));

        return "notifications";
    }
}