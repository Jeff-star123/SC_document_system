package com.sjc.schooldocuments.common;

import com.sjc.schooldocuments.notification.NotificationService;
import com.sjc.schooldocuments.user.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Publishes the signed-in account and its unread notification count to every
 * view, so the shared header never needs a per-controller lookup.
 */
@ControllerAdvice
public class GlobalModelAdvice {

    private final CurrentUserService currentUserService;
    private final NotificationService notificationService;
    private final String institutionName;
    private final String systemName;

    public GlobalModelAdvice(CurrentUserService currentUserService, NotificationService notificationService,
                             @Value("${app.institution.name}") String institutionName,
                             @Value("${app.institution.system-name}") String systemName) {
        this.currentUserService = currentUserService;
        this.notificationService = notificationService;
        this.institutionName = institutionName;
        this.systemName = systemName;
    }

    @ModelAttribute("currentUser")
    public User currentUser() {
        return currentUserService.current().orElse(null);
    }

    @ModelAttribute("unreadCount")
    public long unreadCount() {
        return currentUserService.current()
                .map(user -> notificationService.unreadCountFor(user.getId()))
                .orElse(0L);
    }

    @ModelAttribute("institutionName")
    public String institutionName() {
        return institutionName;
    }

    @ModelAttribute("systemName")
    public String systemName() {
        return systemName;
    }
}
