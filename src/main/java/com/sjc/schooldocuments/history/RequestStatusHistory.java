package com.sjc.schooldocuments.history;

import com.sjc.schooldocuments.request.DocumentRequest;
import com.sjc.schooldocuments.user.User;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Immutable trail of every status or approval change. Rows are only ever inserted.
 */
@Entity
@Table(name = "request_status_history")
public class RequestStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "request_id", nullable = false)
    private DocumentRequest request;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "changed_by", nullable = false)
    private User changedBy;

    @Column(name = "old_status", length = 50)
    private String oldStatus;

    @Column(name = "new_status", length = 50)
    private String newStatus;

    @Column(name = "old_approval_status", length = 50)
    private String oldApprovalStatus;

    @Column(name = "new_approval_status", length = 50)
    private String newApprovalStatus;

    @Column(name = "remarks", columnDefinition = "TEXT")
    private String remarks;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public DocumentRequest getRequest() {
        return request;
    }

    public void setRequest(DocumentRequest request) {
        this.request = request;
    }

    public User getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(User changedBy) {
        this.changedBy = changedBy;
    }

    public String getOldStatus() {
        return oldStatus;
    }

    public void setOldStatus(String oldStatus) {
        this.oldStatus = oldStatus;
    }

    public String getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(String newStatus) {
        this.newStatus = newStatus;
    }

    public String getOldApprovalStatus() {
        return oldApprovalStatus;
    }

    public void setOldApprovalStatus(String oldApprovalStatus) {
        this.oldApprovalStatus = oldApprovalStatus;
    }

    public String getNewApprovalStatus() {
        return newApprovalStatus;
    }

    public void setNewApprovalStatus(String newApprovalStatus) {
        this.newApprovalStatus = newApprovalStatus;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
