package com.sjc.schooldocuments.request.dto;

import com.sjc.schooldocuments.request.ApprovalStatus;
import com.sjc.schooldocuments.request.DocumentRequest;
import com.sjc.schooldocuments.request.RequestStatus;

import java.time.LocalDateTime;

/**
 * Flattened row for request tables. Built inside a transaction so lazy
 * associations never leak into the view layer.
 */
public class RequestSummaryDto {

    private final Long id;
    private final String referenceNumber;
    private final String studentName;
    private final String studentNumber;
    private final String documentTypeName;
    private final int quantity;
    private final RequestStatus status;
    private final ApprovalStatus approvalStatus;
    private final String assignedStaffName;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final boolean claimed;

    public RequestSummaryDto(Long id, String referenceNumber, String studentName, String studentNumber,
                             String documentTypeName, int quantity, RequestStatus status,
                             ApprovalStatus approvalStatus, String assignedStaffName,
                             LocalDateTime createdAt, LocalDateTime updatedAt, boolean claimed) {
        this.id = id;
        this.referenceNumber = referenceNumber;
        this.studentName = studentName;
        this.studentNumber = studentNumber;
        this.documentTypeName = documentTypeName;
        this.quantity = quantity;
        this.status = status;
        this.approvalStatus = approvalStatus;
        this.assignedStaffName = assignedStaffName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.claimed = claimed;
    }

    public static RequestSummaryDto from(DocumentRequest r) {
        return new RequestSummaryDto(
                r.getId(),
                r.getReferenceNumber(),
                r.getStudent().getFullName(),
                r.getStudent().getStudentNumber(),
                r.getDocumentType().getName(),
                r.getQuantity(),
                r.getStatus(),
                r.getApprovalStatus(),
                r.getAssignedStaff() == null ? null : r.getAssignedStaff().getFullName(),
                r.getCreatedAt(),
                r.getUpdatedAt(),
                r.getClaimedAt() != null);
    }

    public Long getId() {
        return id;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getStudentNumber() {
        return studentNumber;
    }

    public String getDocumentTypeName() {
        return documentTypeName;
    }

    public int getQuantity() {
        return quantity;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public ApprovalStatus getApprovalStatus() {
        return approvalStatus;
    }

    public String getAssignedStaffName() {
        return assignedStaffName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public boolean isClaimed() {
        return claimed;
    }
}
