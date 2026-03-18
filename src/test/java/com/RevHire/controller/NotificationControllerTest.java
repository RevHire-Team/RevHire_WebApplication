package com.RevHire.controller;

import com.RevHire.dto.NotificationDTO;
import com.RevHire.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NotificationController.class)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService notificationService;

    private MockHttpSession session;
    private NotificationDTO mockNotification;

    @BeforeEach
    void setUp() {
        session = new MockHttpSession();

        mockNotification = new NotificationDTO();
        mockNotification.setNotificationId(1L);
        mockNotification.setMessage("Your application was viewed");
        mockNotification.setIsRead(false);
    }

    // ========================= HTML View Tests =========================

    @Test
    void showNotificationsPage_ShouldReturnView_WhenLoggedIn() throws Exception {
        // Specifically setting "userId" in session as per controller logic
        session.setAttribute("userId", 123L);

        mockMvc.perform(get("/notifications/notification").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("notifications"));
    }

    @Test
    void showNotificationsPage_ShouldRedirect_WhenNotLoggedIn() throws Exception {
        mockMvc.perform(get("/notifications/notification")) // Empty session
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login"));
    }

    // ========================= JSON / API Tests =========================

    @Test
    void getNotifications_ShouldReturnJsonList() throws Exception {
        List<NotificationDTO> notifications = Arrays.asList(mockNotification);
        when(notificationService.getUserNotifications(1L)).thenReturn(notifications);

        mockMvc.perform(get("/notifications/api/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].message").value("Your application was viewed"))
                // CHANGE: Use isRead and notificationId to match your DTO's JSON output
                .andExpect(jsonPath("$[0].isRead").value(false))
                .andExpect(jsonPath("$[0].notificationId").value(1));

        verify(notificationService, times(1)).getUserNotifications(1L);
    }

    @Test
    void markRead_ShouldReturnOkStatus() throws Exception {
        // Void method in service, so we just verify it's called
        doNothing().when(notificationService).markAsRead(1L);

        mockMvc.perform(put("/notifications/api/1/read"))
                .andExpect(status().isOk());

        verify(notificationService, times(1)).markAsRead(1L);
    }
}