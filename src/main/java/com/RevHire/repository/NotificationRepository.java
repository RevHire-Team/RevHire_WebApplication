package com.RevHire.repository;

import java.util.List;

//import com.RevHire.dto.NotificationDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import com.RevHire.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

//    List<NotificationDTO> findByUserUserId(Long userId);

}
