package com.sjc.schooldocuments.notification;

import com.sjc.schooldocuments.common.CurrentUserService;
import com.sjc.schooldocuments.notification.dto.NotificationDto;
import com.sjc.schooldocuments.request.DocumentRequest;
import com.sjc.schooldocuments.user.AccountStatus;
import com.sjc.schooldocuments.user.Role;
import com.sjc.schooldocuments.user.User;
import com.sjc.schooldocuments.user.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository repository;
    private final UserRepository userRepository;
    private final CurrentUserService currentUserService;

    public NotificationService(NotificationRepository repository, UserRepository userRepository,
                               CurrentUserService currentUserService) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.currentUserService = currentUserService;
    }

    @Transactional
    public void notifyUser(User recipient, DocumentRequest request, NotificationType type,
                           String title, String message) {
        Notification notification = new Notification();
        notification.setUser(recipient);
        notification.setRequest(request);
        notification.setNotificationType(type);
        notification.setTitle(title);
        notification.setMessage(message);
        repository.save(notification);
    }

    /**
     * Tells every active administrator that a request is waiting for a decision.
     */
    @Transactional
    public void notifyAdministrators(DocumentRequest request, NotificationType type,
                                     String title, String message) {
        for (User admin : userRepository.findByRoleAndAccountStatus(Role.ADMIN, AccountStatus.ACTIVE)) {
            notifyUser(admin, request, type, title, message);
        }
    }

    @Transactional(readOnly = true)
    public Page<NotificationDto> pageForCurrentUser(int page, int size) {
        User user = currentUserService.require();
        return repository.findByUserIdOrderByCreatedAtDesc(user.getId(),
                PageRequest.of(Math.max(0, page), Math.min(Math.max(1, size), 50))).map(NotificationDto::from);
    }

    @Transactional(readOnly = true)
    public List<NotificationDto> latestForCurrentUser(int limit) {
        User user = currentUserService.require();
        return repository.findTop5ByUserIdAndReadFalseOrderByCreatedAtDesc(user.getId())
                .stream().limit(limit).map(NotificationDto::from).toList();
    }

    @Transactional(readOnly = true)
    public long unreadCountForCurrentUser() {
        return currentUserService.current()
                .map(user -> repository.countByUserIdAndReadFalse(user.getId()))
                .orElse(0L);
    }

    @Transactional(readOnly = true)
    public long unreadCountFor(Long userId) {
        return repository.countByUserIdAndReadFalse(userId);
    }

    @Transactional
    public void markRead(Long notificationId) {
        User user = currentUserService.require();
        Notification notification = repository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("That notification no longer exists."));
        if (!notification.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Notifications belong to the account that received them.");
        }
        if (!notification.isRead()) {
            notification.setRead(true);
            notification.setReadAt(LocalDateTime.now());
            repository.save(notification);
        }
    }

    @Transactional
    public int markAllRead() {
        User user = currentUserService.require();
        return repository.markAllReadForUser(user.getId(), LocalDateTime.now());
    }
}
