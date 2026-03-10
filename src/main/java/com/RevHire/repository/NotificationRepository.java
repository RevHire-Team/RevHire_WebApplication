package com.RevHire.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.RevHire.entity.Notification;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Logger logger = LogManager.getLogger(NotificationRepository.class);

    List<Notification> findByUserUserId(Long userId);
}