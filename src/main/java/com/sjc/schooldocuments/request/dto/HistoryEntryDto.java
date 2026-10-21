package com.sjc.schooldocuments.request.dto;

import com.sjc.schooldocuments.history.RequestStatusHistory;

import java.time.LocalDateTime;

public class HistoryEntryDto {

    private final String actorName;
    private final String actorRole;
    private final String oldStatus;
    private final String newStatus;
    private final String oldApprovalStatus;
    private final String newApprovalStatus;
    private final String remarks;
    private final LocalDateTime createdAt;

    public HistoryEntryDto(String actorName, String actorRole, String oldStatus, String newStatus,
                           String oldApprovalStatus, String newApprovalStatus, String remarks,
                           LocalDateTime createdAt) {
        this.actorName = actorName;
        this.actorRole = actorRole;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.oldApprovalStatus = oldApprovalStatus;
        this.newApprovalStatus = newApprovalStatus;
        this.remarks = remarks;
        this.createdAt = createdAt;
    }

    public static HistoryEntryDto from(RequestStatusHistory h) {
        return new HistoryEntryDto(
                h.getChangedBy().getFullName(),
                h.getChangedBy().getRole().getLabel(),
                h.getOldStatus(),
                h.getNewStatus(),
                h.getOldApprovalStatus(),
                h.getNewApprovalStatus(),
                h.getRemarks(),
                h.getCreatedAt());
    }

    public String getActorName() {
        return actorName;
    }

    public String getActorRole() {
        return actorRole;
    }

    public String getOldStatus() {
        return oldStatus;
    }

    public String getNewStatus() {
        return newStatus;
    }

    public String getOldApprovalStatus() {
        return oldApprovalStatus;
    }

    public String getNewApprovalStatus() {
        return newApprovalStatus;
    }

    public String getRemarks() {
        return remarks;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public boolean isStatusChanged() {
        return newStatus != null && !newStatus.equals(oldStatus);
    }

    public boolean isApprovalChanged() {
        return newApprovalStatus != null && !newApprovalStatus.equals(oldApprovalStatus);
    }
}
