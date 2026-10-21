package com.sjc.schooldocuments.notification.dto;

import com.sjc.schooldocuments.notification.Notification;
import com.sjc.schooldocuments.notification.NotificationType;

import java.time.LocalDateTime;

public class NotificationDto {

    private final Long id;
    private final String title;
    private final String message;
    private final NotificationType type;
    private final boolean read;
    private final LocalDateTime createdAt;
    private final Long requestId;
    private final String referenceNumber;

    public NotificationDto(Long id, String title, String message, NotificationType type, boolean read,
                           LocalDateTime createdAt, Long requestId, String referenceNumber) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.type = type;
        this.read = read;
        this.createdAt = createdAt;
        this.requestId = requestId;
        this.referenceNumber = referenceNumber;
    }

    public static NotificationDto from(Notification n) {
        return new NotificationDto(n.getId(), n.getTitle(), n.getMessage(), n.getNotificationType(),
                n.isRead(), n.getCreatedAt(),
                n.getRequest() == null ? null : n.getRequest().getId(),
                n.getRequest() == null ? null : n.getRequest().getReferenceNumber());
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public NotificationType getType() {
        return type;
    }

    public boolean isRead() {
        return read;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Long getRequestId() {
        return requestId;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }
}
