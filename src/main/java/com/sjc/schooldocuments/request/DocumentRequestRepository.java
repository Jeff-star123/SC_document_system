package com.sjc.schooldocuments.request;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface DocumentRequestRepository
        extends JpaRepository<DocumentRequest, Long>, JpaSpecificationExecutor<DocumentRequest> {

    Optional<DocumentRequest> findByReferenceNumber(String referenceNumber);

    Page<DocumentRequest> findByStudentId(Long studentId, Pageable pageable);

    List<DocumentRequest> findTop5ByStudentIdOrderByCreatedAtDesc(Long studentId);

    List<DocumentRequest> findByApprovalStatusOrderByCreatedAtAsc(ApprovalStatus approvalStatus);

    List<DocumentRequest> findByStatusOrderByUpdatedAtAsc(RequestStatus status);

    long countByStudentId(Long studentId);

    long countByStudentIdAndStatus(Long studentId, RequestStatus status);

    long countByStudentIdAndApprovalStatus(Long studentId, ApprovalStatus approvalStatus);

    long countByStatus(RequestStatus status);

    long countByDocumentTypeIdAndStatusNot(Long documentTypeId, RequestStatus status);

    long countByApprovalStatus(ApprovalStatus approvalStatus);

    long countByClaimedAtIsNotNull();

    long countByClaimedAtBetween(LocalDateTime from, LocalDateTime to);

    long countByCreatedAtBetween(LocalDateTime from, LocalDateTime to);

    long countByStatusAndCreatedAtBetween(RequestStatus status, LocalDateTime from, LocalDateTime to);

    long countByApprovalStatusAndCreatedAtBetween(ApprovalStatus status, LocalDateTime from, LocalDateTime to);

    long countByClaimedAtIsNotNullAndCreatedAtBetween(LocalDateTime from, LocalDateTime to);

    boolean existsByStudentIdAndDocumentTypeIdAndPurposeAndCreatedAtAfter(
            Long studentId, Long documentTypeId, String purpose, LocalDateTime after);

    java.util.Optional<DocumentRequest> findTopByStudentIdAndDocumentTypeIdAndPurposeAndCreatedAtAfterOrderByCreatedAtDesc(
            Long studentId, Long documentTypeId, String purpose, LocalDateTime after);

    @Query("select r.status, count(r) from DocumentRequest r where r.createdAt between ?1 and ?2 group by r.status")
    List<Object[]> countGroupedByStatus(LocalDateTime from, LocalDateTime to);

    @Query("select r.documentType.name, count(r) from DocumentRequest r where r.createdAt between ?1 and ?2 "
            + "group by r.documentType.name order by count(r) desc")
    List<Object[]> countGroupedByDocumentType(LocalDateTime from, LocalDateTime to);

    @Query("select r.approvalStatus, count(r) from DocumentRequest r where r.createdAt between ?1 and ?2 "
            + "group by r.approvalStatus")
    List<Object[]> countGroupedByApprovalStatus(LocalDateTime from, LocalDateTime to);

    @Query(value = "select date_format(created_at, '%Y-%m') as period, count(*) from document_requests "
            + "where created_at between ?1 and ?2 group by period order by period", nativeQuery = true)
    List<Object[]> countGroupedByMonth(LocalDateTime from, LocalDateTime to);

    @Query("select count(r) from DocumentRequest r where r.approvalStatus = com.sjc.schooldocuments.request.ApprovalStatus.PENDING")
    long countAwaitingApproval();

    @Query("select count(r) from DocumentRequest r where r.status = com.sjc.schooldocuments.request.RequestStatus.PENDING "
            + "and r.approvalStatus in (com.sjc.schooldocuments.request.ApprovalStatus.NOT_REQUIRED, "
            + "com.sjc.schooldocuments.request.ApprovalStatus.APPROVED)")
    long countReadyToProcess();

    @Query("select count(r) from DocumentRequest r where r.assignedStaff.id = ?1 and r.status = ?2")
    long countByAssignedStaffAndStatus(Long staffId, RequestStatus status);
}
