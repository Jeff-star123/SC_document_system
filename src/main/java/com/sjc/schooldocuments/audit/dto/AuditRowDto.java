package com.sjc.schooldocuments.audit.dto;

import com.sjc.schooldocuments.audit.AuditLog;

import java.time.LocalDateTime;

public class AuditRowDto {

    private final Long id;
    private final String actorName;
    private final String action;
    private final String entityType;
    private final Long entityId;
    private final String description;
    private final LocalDateTime createdAt;

    public AuditRowDto(Long id, String actorName, String action, String entityType, Long entityId,
                       String description, LocalDateTime createdAt) {
        this.id = id;
        this.actorName = actorName;
        this.action = action;
        this.entityType = entityType;
        this.entityId = entityId;
        this.description = description;
        this.createdAt = createdAt;
    }

    public static AuditRowDto from(AuditLog log) {
        return new AuditRowDto(log.getId(),
                log.getActor() == null ? "System" : log.getActor().getFullName(),
                log.getAction(), log.getEntityType(), log.getEntityId(), log.getDescription(), log.getCreatedAt());
    }

    public Long getId() {
        return id;
    }

    public String getActorName() {
        return actorName;
    }

    public String getAction() {
        return action;
    }

    public String getEntityType() {
        return entityType;
    }

    public Long getEntityId() {
        return entityId;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
