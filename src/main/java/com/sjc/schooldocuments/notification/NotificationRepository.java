package com.sjc.schooldocuments.notification;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Page<Notification> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    List<Notification> findTop5ByUserIdAndReadFalseOrderByCreatedAtDesc(Long userId);

    long countByUserIdAndReadFalse(Long userId);

    long countByUserId(Long userId);

    @Modifying
    @Query("update Notification n set n.read = true, n.readAt = ?2 where n.user.id = ?1 and n.read = false")
    int markAllReadForUser(Long userId, LocalDateTime readAt);

    List<Notification> findByRequestIdAndUserId(Long requestId, Long userId);
}
