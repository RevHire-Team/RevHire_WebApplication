package com.RevHire.controller;

import com.RevHire.dto.NotificationDTO;
import com.RevHire.service.NotificationService;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NotificationController.class)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService notificationService;

    // ================= SHOW PAGE =================

    @Test
    void testShowNotificationsPage() throws Exception {

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("userId", 1L);

        mockMvc.perform(get("/notifications/notification").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("notifications"));
    }

    // ================= GET NOTIFICATIONS =================

    @Test
    void testGetNotifications() throws Exception {

        NotificationDTO notification = mock(NotificationDTO.class);

        when(notification.getIsRead()).thenReturn(false);

        when(notificationService.getUserNotifications(1L))
                .thenReturn(List.of(notification));

        mockMvc.perform(get("/notifications/api/1"))
                .andExpect(status().isOk());
    }

    // ================= UNREAD COUNT =================

    @Test
    void testUnreadCount() throws Exception {

        NotificationDTO n1 = mock(NotificationDTO.class);
        NotificationDTO n2 = mock(NotificationDTO.class);

        when(n1.getIsRead()).thenReturn(false);
        when(n2.getIsRead()).thenReturn(true);

        when(notificationService.getUserNotifications(1L))
                .thenReturn(List.of(n1, n2));

        mockMvc.perform(get("/notifications/api/unread-count/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(1));
    }

    // ================= MARK READ =================

    @Test
    void testMarkRead() throws Exception {

        doNothing().when(notificationService).markAsRead(1L);

        mockMvc.perform(put("/notifications/api/1/read"))
                .andExpect(status().isOk());
    }
}