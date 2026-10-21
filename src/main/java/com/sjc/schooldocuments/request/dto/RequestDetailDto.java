package com.sjc.schooldocuments.request.dto;

import com.sjc.schooldocuments.request.ApprovalStatus;
import com.sjc.schooldocuments.request.DocumentRequest;
import com.sjc.schooldocuments.request.RequestStatus;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Everything a request detail page needs, assembled inside the service
 * transaction so the view never touches lazy collections.
 */
public class RequestDetailDto {

    private final Long id;
    private final String referenceNumber;
    private final Long studentId;
    private final String studentName;
    private final String studentNumber;
    private final String studentCourse;
    private final String studentYearLevel;
    private final String studentEmail;
    private final Long documentTypeId;
    private final String documentTypeName;
    private final boolean documentTypeRequiresApproval;
    private final String documentTypeInstructions;
    private final String purpose;
    private final int quantity;
    private final String additionalDetails;
    private final RequestStatus status;
    private final ApprovalStatus approvalStatus;
    private final String approvedByName;
    private final LocalDateTime approvedAt;
    private final String rejectionReason;
    private final String assignedStaffName;
    private final String staffNotes;
    private final String pickupInstructions;
    private final LocalDateTime claimedAt;
    private final String releasedByName;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final List<HistoryEntryDto> history;

    @SuppressWarnings("java:S107")
    public RequestDetailDto(Long id, String referenceNumber, Long studentId, String studentName,
                            String studentNumber, String studentCourse, String studentYearLevel,
                            String studentEmail, Long documentTypeId, String documentTypeName,
                            boolean documentTypeRequiresApproval, String documentTypeInstructions,
                            String purpose, int quantity, String additionalDetails, RequestStatus status,
                            ApprovalStatus approvalStatus, String approvedByName, LocalDateTime approvedAt,
                            String rejectionReason, String assignedStaffName, String staffNotes,
                            String pickupInstructions, LocalDateTime claimedAt, String releasedByName,
                            LocalDateTime createdAt, LocalDateTime updatedAt, List<HistoryEntryDto> history) {
        this.id = id;
        this.referenceNumber = referenceNumber;
        this.studentId = studentId;
        this.studentName = studentName;
        this.studentNumber = studentNumber;
        this.studentCourse = studentCourse;
        this.studentYearLevel = studentYearLevel;
        this.studentEmail = studentEmail;
        this.documentTypeId = documentTypeId;
        this.documentTypeName = documentTypeName;
        this.documentTypeRequiresApproval = documentTypeRequiresApproval;
        this.documentTypeInstructions = documentTypeInstructions;
        this.purpose = purpose;
        this.quantity = quantity;
        this.additionalDetails = additionalDetails;
        this.status = status;
        this.approvalStatus = approvalStatus;
        this.approvedByName = approvedByName;
        this.approvedAt = approvedAt;
        this.rejectionReason = rejectionReason;
        this.assignedStaffName = assignedStaffName;
        this.staffNotes = staffNotes;
        this.pickupInstructions = pickupInstructions;
        this.claimedAt = claimedAt;
        this.releasedByName = releasedByName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.history = history;
    }

    public static RequestDetailDto from(DocumentRequest r, List<HistoryEntryDto> history) {
        return new RequestDetailDto(
                r.getId(),
                r.getReferenceNumber(),
                r.getStudent().getId(),
                r.getStudent().getFullName(),
                r.getStudent().getStudentNumber(),
                r.getStudent().getCourseOrDepartment(),
                r.getStudent().getYearLevelOrSection(),
                r.getStudent().getEmail(),
                r.getDocumentType().getId(),
                r.getDocumentType().getName(),
                r.getDocumentType().isRequiresAdminApproval(),
                r.getDocumentType().getInstructions(),
                r.getPurpose(),
                r.getQuantity(),
                r.getAdditionalDetails(),
                r.getStatus(),
                r.getApprovalStatus(),
                r.getApprovedBy() == null ? null : r.getApprovedBy().getFullName(),
                r.getApprovedAt(),
                r.getRejectionReason(),
                r.getAssignedStaff() == null ? null : r.getAssignedStaff().getFullName(),
                r.getStaffNotes(),
                r.getPickupInstructions(),
                r.getClaimedAt(),
                r.getReleasedBy() == null ? null : r.getReleasedBy().getFullName(),
                r.getCreatedAt(),
                r.getUpdatedAt(),
                history);
    }

    public Long getId() {
        return id;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public Long getStudentId() {
        return studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getStudentNumber() {
        return studentNumber;
    }

    public String getStudentCourse() {
        return studentCourse;
    }

    public String getStudentYearLevel() {
        return studentYearLevel;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public Long getDocumentTypeId() {
        return documentTypeId;
    }

    public String getDocumentTypeName() {
        return documentTypeName;
    }

    public boolean isDocumentTypeRequiresApproval() {
        return documentTypeRequiresApproval;
    }

    public String getDocumentTypeInstructions() {
        return documentTypeInstructions;
    }

    public String getPurpose() {
        return purpose;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getAdditionalDetails() {
        return additionalDetails;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public ApprovalStatus getApprovalStatus() {
        return approvalStatus;
    }

    public String getApprovedByName() {
        return approvedByName;
    }

    public LocalDateTime getApprovedAt() {
        return approvedAt;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public String getAssignedStaffName() {
        return assignedStaffName;
    }

    public String getStaffNotes() {
        return staffNotes;
    }

    public String getPickupInstructions() {
        return pickupInstructions;
    }

    public LocalDateTime getClaimedAt() {
        return claimedAt;
    }

    public String getReleasedByName() {
        return releasedByName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public List<HistoryEntryDto> getHistory() {
        return history;
    }

    public boolean isRejected() {
        return approvalStatus == ApprovalStatus.REJECTED;
    }

    public boolean isAwaitingApproval() {
        return approvalStatus == ApprovalStatus.PENDING;
    }

    public boolean isClaimed() {
        return claimedAt != null;
    }
}
