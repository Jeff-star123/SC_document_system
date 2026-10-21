package com.sjc.schooldocuments.request;

import com.sjc.schooldocuments.audit.AuditService;
import com.sjc.schooldocuments.common.ReferenceNumberFormatter;
import com.sjc.schooldocuments.common.exception.BusinessRuleException;
import com.sjc.schooldocuments.common.exception.ResourceNotFoundException;
import com.sjc.schooldocuments.documenttype.DocumentType;
import com.sjc.schooldocuments.documenttype.DocumentTypeRepository;
import com.sjc.schooldocuments.history.RequestStatusHistory;
import com.sjc.schooldocuments.history.RequestStatusHistoryRepository;
import com.sjc.schooldocuments.notification.NotificationService;
import com.sjc.schooldocuments.notification.NotificationType;
import com.sjc.schooldocuments.request.dto.*;
import com.sjc.schooldocuments.settings.SettingsService;
import com.sjc.schooldocuments.user.Role;
import com.sjc.schooldocuments.user.User;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class RequestService {

    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final int DUPLICATE_WINDOW_SECONDS = 60;
    private static final int CSV_ROW_LIMIT = 5000;

    private final DocumentRequestRepository requestRepository;
    private final DocumentTypeRepository documentTypeRepository;
    private final RequestStatusHistoryRepository historyRepository;
    private final NotificationService notificationService;
    private final SettingsService settingsService;
    private final AuditService auditService;
    private final ReferenceNumberFormatter referenceNumberFormatter;

    public RequestService(DocumentRequestRepository requestRepository,
                          DocumentTypeRepository documentTypeRepository,
                          RequestStatusHistoryRepository historyRepository,
                          NotificationService notificationService,
                          SettingsService settingsService,
                          AuditService auditService,
                          ReferenceNumberFormatter referenceNumberFormatter) {
        this.requestRepository = requestRepository;
        this.documentTypeRepository = documentTypeRepository;
        this.historyRepository = historyRepository;
        this.notificationService = notificationService;
        this.settingsService = settingsService;
        this.auditService = auditService;
        this.referenceNumberFormatter = referenceNumberFormatter;
    }

    // ------------------------------------------------------------------
    // Student: submit a request
    // ------------------------------------------------------------------

    @Transactional
    public String createRequest(User student, RequestCreateForm form) {
        DocumentType documentType = documentTypeRepository.findById(form.getDocumentTypeId())
                .filter(DocumentType::isActive)
                .orElseThrow(() -> new BusinessRuleException(
                        "That document type is not available. Choose from the current list."));

        String purpose = form.getPurpose().trim();
        LocalDateTime duplicateWindow = LocalDateTime.now().minusSeconds(DUPLICATE_WINDOW_SECONDS);
        var duplicate = requestRepository
                .findTopByStudentIdAndDocumentTypeIdAndPurposeAndCreatedAtAfterOrderByCreatedAtDesc(
                        student.getId(), documentType.getId(), purpose, duplicateWindow);
        if (duplicate.isPresent()) {
            throw new BusinessRuleException(
                    "This looks like a duplicate of request " + duplicate.get().getReferenceNumber()
                            + ", submitted moments ago. Track that request instead of submitting again.");
        }

        DocumentRequest request = new DocumentRequest();
        request.setReferenceNumber(referenceNumberFormatter.format(
                referenceNumberFormatter.currentYear(), settingsService.nextRequestSequence()));
        request.setStudent(student);
        request.setDocumentType(documentType);
        request.setPurpose(purpose);
        request.setQuantity(form.getQuantity());
        request.setAdditionalDetails(blankToNull(form.getAdditionalDetails()));
        request.setStatus(RequestStatus.PENDING);
        request.setApprovalStatus(documentType.isRequiresAdminApproval()
                ? ApprovalStatus.PENDING
                : ApprovalStatus.NOT_REQUIRED);
        DocumentRequest saved = requestRepository.save(request);

        recordHistory(saved, student, null, RequestStatus.PENDING.name(),
                null, saved.getApprovalStatus().name(), "Request submitted by the student.");

        notificationService.notifyUser(student, saved,
                saved.getApprovalStatus() == ApprovalStatus.PENDING
                        ? NotificationType.REQUEST_SUBMITTED
                        : NotificationType.REQUEST_SUBMITTED,
                "Request received — " + saved.getReferenceNumber(),
                "We received your request for " + documentType.getName() + "."
                        + (saved.getApprovalStatus() == ApprovalStatus.PENDING
                        ? " It is now waiting for administrator approval."
                        : " The registrar will begin processing it shortly."));

        if (saved.getApprovalStatus() == ApprovalStatus.PENDING) {
            notificationService.notifyAdministrators(saved, NotificationType.APPROVAL_REQUIRED,
                    "Approval needed — " + saved.getReferenceNumber(),
                    student.getFullName() + " (" + student.getStudentNumber() + ") requested "
                            + documentType.getName() + ". Review it in the approval queue.");
        }

        auditService.record("REQUEST_SUBMITTED", "DocumentRequest", saved.getId(),
                "Request " + saved.getReferenceNumber() + " submitted for " + documentType.getName());

        return saved.getReferenceNumber();
    }

    // ------------------------------------------------------------------
    // Read models
    // ------------------------------------------------------------------

    @Transactional(readOnly = true)
    public Page<RequestSummaryDto> findForStudent(Long studentId, RequestFilter filter) {
        Pageable pageable = pageableFor(filter, "createdAt");
        Specification<DocumentRequest> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("student").get("id"), studentId));
            if (filter.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), filter.getStatus()));
            }
            if (filter.getDocumentTypeId() != null) {
                predicates.add(cb.equal(root.get("documentType").get("id"), filter.getDocumentTypeId()));
            }
            if (filter.getDateFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), filter.getDateFrom().atStartOfDay()));
            }
            if (filter.getDateTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), filter.getDateTo().atTime(23, 59, 59)));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        return requestRepository.findAll(spec, pageable).map(RequestSummaryDto::from);
    }

    @Transactional(readOnly = true)
    public Page<RequestSummaryDto> search(RequestFilter filter) {
        Pageable pageable = pageableFor(filter, "createdAt");
        return requestRepository.findAll(buildSearchSpecification(filter), pageable).map(RequestSummaryDto::from);
    }

    @Transactional(readOnly = true)
    public List<RequestSummaryDto> recent(int limit) {
        Pageable pageable = PageRequest.of(0, Math.max(1, limit), Sort.by(Sort.Direction.DESC, "createdAt"));
        return requestRepository.findAll(pageable).map(RequestSummaryDto::from).getContent();
    }

    @Transactional(readOnly = true)
    public RequestDetailDto getDetail(Long id, User viewer) {
        DocumentRequest request = require(id);
        if (viewer.getRole() == Role.STUDENT && !request.getStudent().getId().equals(viewer.getId())) {
            throw new AccessDeniedException("Students can only open their own requests.");
        }
        List<HistoryEntryDto> history = historyRepository.findByRequestIdOrderByCreatedAtAsc(id)
                .stream().map(HistoryEntryDto::from).toList();
        return RequestDetailDto.from(request, history);
    }

    // ------------------------------------------------------------------
    // Registrar actions
    // ------------------------------------------------------------------

    @Transactional
    public void startProcessing(Long id, User staff, ProcessRequestForm form) {
        DocumentRequest request = require(id);
        if (request.getStatus() == RequestStatus.PROCESSING) {
            throw new BusinessRuleException("This request is already being processed.");
        }
        if (request.getStatus() == RequestStatus.READY_FOR_PICKUP) {
            throw new BusinessRuleException("This request is already ready for pick-up.");
        }
        if (request.getApprovalStatus() == ApprovalStatus.PENDING) {
            throw new BusinessRuleException(
                    "This request still needs administrator approval before processing can begin.");
        }
        if (request.getApprovalStatus() == ApprovalStatus.REJECTED) {
            throw new BusinessRuleException(
                    "This request was rejected and cannot be processed. The student has been notified.");
        }

        String oldStatus = request.getStatus().name();
        request.setStatus(RequestStatus.PROCESSING);
        request.setAssignedStaff(staff);
        if (form.getStaffNotes() != null && !form.getStaffNotes().isBlank()) {
            request.setStaffNotes(form.getStaffNotes().trim());
        }
        requestRepository.save(request);

        String remarks = form.getRemarks() == null || form.getRemarks().isBlank()
                ? "Verified and moved to processing."
                : form.getRemarks().trim();
        recordHistory(request, staff, oldStatus, RequestStatus.PROCESSING.name(),
                request.getApprovalStatus().name(), request.getApprovalStatus().name(), remarks);

        notificationService.notifyUser(request.getStudent(), request, NotificationType.REQUEST_PROCESSING,
                "Processing started — " + request.getReferenceNumber(),
                "The registrar is now preparing your " + request.getDocumentType().getName()
                        + ". We will notify you again when it is ready for pick-up.");

        auditService.record("REQUEST_PROCESSING", "DocumentRequest", request.getId(),
                "Request " + request.getReferenceNumber() + " moved to processing.");
    }

    @Transactional
    public void markReadyForPickup(Long id, User staff, ReadyForPickupForm form) {
        DocumentRequest request = require(id);
        if (request.getStatus() == RequestStatus.READY_FOR_PICKUP) {
            throw new BusinessRuleException("This request is already marked ready for pick-up.");
        }
        if (request.getStatus() != RequestStatus.PROCESSING) {
            throw new BusinessRuleException("Start processing this request before marking it ready for pick-up.");
        }
        if (form.getPickupInstructions() == null || form.getPickupInstructions().isBlank()) {
            throw new BusinessRuleException("Pick-up instructions are required so the student knows what to bring.");
        }

        String oldStatus = request.getStatus().name();
        request.setStatus(RequestStatus.READY_FOR_PICKUP);
        request.setPickupInstructions(form.getPickupInstructions().trim());
        if (form.getStaffNotes() != null && !form.getStaffNotes().isBlank()) {
            request.setStaffNotes(form.getStaffNotes().trim());
        }
        requestRepository.save(request);

        recordHistory(request, staff, oldStatus, RequestStatus.READY_FOR_PICKUP.name(),
                request.getApprovalStatus().name(), request.getApprovalStatus().name(),
                "Document prepared. Student notified for pick-up.");

        String location = settingsService.value(SettingsService.PICKUP_LOCATION, "the Registrar's Office");
        String hours = settingsService.value(SettingsService.OFFICE_HOURS, "");
        String contact = settingsService.value(SettingsService.CONTACT_EMAIL, "");
        StringBuilder message = new StringBuilder()
                .append(request.getDocumentType().getName())
                .append(" (x").append(request.getQuantity()).append(") is ready for pick-up. ")
                .append("Pick up at ").append(location).append(".");
        if (!hours.isBlank()) {
            message.append(" Office hours: ").append(hours).append(".");
        }
        message.append(" Instructions: ").append(request.getPickupInstructions());
        if (!contact.isBlank()) {
            message.append(" Questions? Email ").append(contact).append(".");
        }

        notificationService.notifyUser(request.getStudent(), request, NotificationType.REQUEST_READY,
                "Ready for pick-up — " + request.getReferenceNumber(), message.toString());

        auditService.record("REQUEST_READY", "DocumentRequest", request.getId(),
                "Request " + request.getReferenceNumber() + " marked ready for pick-up by " + staff.getFullName());
    }

    /**
     * Supplementary record of the physical hand-over. The student-facing status
     * stays Ready for Pick-up.
     */
    @Transactional
    public void recordRelease(Long id, User staff, ReleaseRecordForm form) {
        DocumentRequest request = require(id);
        if (request.getStatus() != RequestStatus.READY_FOR_PICKUP) {
            throw new BusinessRuleException("Only requests that are ready for pick-up can be released.");
        }
        if (request.getClaimedAt() != null) {
            throw new BusinessRuleException("A release for this request is already on record.");
        }
        LocalDate claimedOn = form.getClaimedOn() == null ? LocalDate.now() : form.getClaimedOn();
        if (claimedOn.isAfter(LocalDate.now())) {
            throw new BusinessRuleException("The claimed date cannot be in the future.");
        }
        request.setClaimedAt(claimedOn.atTime(9, 0));
        request.setReleasedBy(staff);
        requestRepository.save(request);

        String remarks = form.getRemarks() == null || form.getRemarks().isBlank()
                ? "Physical release recorded on " + claimedOn.format(DATE) + "."
                : form.getRemarks().trim();
        recordHistory(request, staff, RequestStatus.READY_FOR_PICKUP.name(), RequestStatus.READY_FOR_PICKUP.name(),
                request.getApprovalStatus().name(), request.getApprovalStatus().name(), remarks);

        auditService.record("REQUEST_RELEASED", "DocumentRequest", request.getId(),
                "Release recorded for " + request.getReferenceNumber() + " on " + claimedOn.format(DATE));
    }

    @Transactional
    public void updateStaffNotes(Long id, User staff, StaffNotesForm form) {
        DocumentRequest request = require(id);
        request.setStaffNotes(blankToNull(form.getStaffNotes()));
        requestRepository.save(request);
        auditService.record("REQUEST_NOTES_UPDATED", "DocumentRequest", request.getId(),
                "Internal notes updated for " + request.getReferenceNumber());
    }

    // ------------------------------------------------------------------
    // Administrative decisions
    // ------------------------------------------------------------------

    @Transactional
    public void decide(Long id, User admin, ApprovalDecisionForm form) {
        if (admin.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("Only administrators can approve or reject requests.");
        }
        DocumentRequest request = require(id);
        if (request.getApprovalStatus() != ApprovalStatus.PENDING) {
            throw new BusinessRuleException("This request is not waiting for an approval decision.");
        }
        if (!form.isApprove() && (form.getReason() == null || form.getReason().isBlank())) {
            throw new BusinessRuleException("Give the student a reason when rejecting a request.");
        }

        String oldApproval = request.getApprovalStatus().name();
        if (form.isApprove()) {
            request.setApprovalStatus(ApprovalStatus.APPROVED);
            request.setRejectionReason(null);
        } else {
            request.setApprovalStatus(ApprovalStatus.REJECTED);
            request.setRejectionReason(form.getReason().trim());
        }
        request.setApprovedBy(admin);
        request.setApprovedAt(LocalDateTime.now());
        requestRepository.save(request);

        recordHistory(request, admin, request.getStatus().name(), request.getStatus().name(),
                oldApproval, request.getApprovalStatus().name(),
                form.isApprove()
                        ? (form.getRemarks() == null || form.getRemarks().isBlank()
                        ? "Approved by administration." : form.getRemarks().trim())
                        : "Rejected: " + request.getRejectionReason());

        if (form.isApprove()) {
            notificationService.notifyUser(request.getStudent(), request, NotificationType.REQUEST_APPROVED,
                    "Request approved — " + request.getReferenceNumber(),
                    "Your request for " + request.getDocumentType().getName()
                            + " was approved. The registrar will begin preparing it.");
        } else {
            notificationService.notifyUser(request.getStudent(), request, NotificationType.REQUEST_REJECTED,
                    "Request rejected — " + request.getReferenceNumber(),
                    "Your request for " + request.getDocumentType().getName()
                            + " was rejected. Reason: " + request.getRejectionReason()
                            + " You may submit a new request once the concern is resolved.");
        }

        auditService.record(form.isApprove() ? "REQUEST_APPROVED" : "REQUEST_REJECTED",
                "DocumentRequest", request.getId(),
                "Request " + request.getReferenceNumber() + (form.isApprove() ? " approved." : " rejected."));
    }

    // ------------------------------------------------------------------
    // CSV export
    // ------------------------------------------------------------------

    @Transactional(readOnly = true)
    public void exportCsv(PrintWriter writer, RequestFilter filter, boolean includeInternalColumns) {
        Pageable pageable = pageableFor(filter, "createdAt");
        pageable = PageRequest.of(0, CSV_ROW_LIMIT, pageable.getSort());
        List<DocumentRequest> rows = requestRepository.findAll(buildSearchSpecification(filter), pageable)
                .getContent();

        writer.println(includeInternalColumns
                ? "Reference,Student number,Student name,Document type,Quantity,Status,Approval,Submitted,Last updated,Assigned staff,Claimed on"
                : "Reference,Document type,Quantity,Status,Submitted,Last updated");
        for (DocumentRequest request : rows) {
            List<String> cells = new ArrayList<>();
            cells.add(request.getReferenceNumber());
            if (includeInternalColumns) {
                cells.add(request.getStudent().getStudentNumber());
                cells.add(request.getStudent().getFullName());
            }
            cells.add(request.getDocumentType().getName());
            cells.add(String.valueOf(request.getQuantity()));
            cells.add(request.getStatus().getLabel());
            if (includeInternalColumns) {
                cells.add(request.getApprovalStatus().getLabel());
            }
            cells.add(request.getCreatedAt().format(DATE_TIME));
            cells.add(request.getUpdatedAt().format(DATE_TIME));
            if (includeInternalColumns) {
                cells.add(request.getAssignedStaff() == null ? "" : request.getAssignedStaff().getFullName());
                cells.add(request.getClaimedAt() == null ? "" : request.getClaimedAt().format(DATE));
            }
            writer.println(cells.stream().map(RequestService::csvCell).reduce((a, b) -> a + "," + b).orElse(""));
        }
        writer.flush();
    }

    // ------------------------------------------------------------------
    // Internals
    // ------------------------------------------------------------------

    private DocumentRequest require(Long id) {
        return requestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("That request could not be found."));
    }

    private void recordHistory(DocumentRequest request, User actor, String oldStatus, String newStatus,
                               String oldApproval, String newApproval, String remarks) {
        RequestStatusHistory history = new RequestStatusHistory();
        history.setRequest(request);
        history.setChangedBy(actor);
        history.setOldStatus(oldStatus);
        history.setNewStatus(newStatus);
        history.setOldApprovalStatus(oldApproval);
        history.setNewApprovalStatus(newApproval);
        history.setRemarks(remarks);
        historyRepository.save(history);
    }

    private Specification<DocumentRequest> buildSearchSpecification(RequestFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (filter.getQ() != null && !filter.getQ().isBlank()) {
                String needle = "%" + filter.getQ().trim().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("referenceNumber")), needle),
                        cb.like(cb.lower(root.get("student").get("studentNumber")), needle),
                        cb.like(cb.lower(root.get("student").get("firstName")), needle),
                        cb.like(cb.lower(root.get("student").get("lastName")), needle),
                        cb.like(cb.lower(root.get("documentType").get("name")), needle)));
            }
            if (filter.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), filter.getStatus()));
            }
            if (filter.getDocumentTypeId() != null) {
                predicates.add(cb.equal(root.get("documentType").get("id"), filter.getDocumentTypeId()));
            }
            if (filter.getDateFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), filter.getDateFrom().atStartOfDay()));
            }
            if (filter.getDateTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), filter.getDateTo().atTime(23, 59, 59)));
            }
            if (filter.isAwaitingApprovalOnly()) {
                predicates.add(cb.equal(root.get("approvalStatus"), ApprovalStatus.PENDING));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * Only whitelisted sort keys reach the query, so column headers can never be
     * used to probe the schema.
     */
    private Pageable pageableFor(RequestFilter filter, String fallbackSort) {
        Map<String, String> allowed = Map.of(
                "reference", "referenceNumber",
                "student", "student.lastName",
                "documentType", "documentType.name",
                "status", "status",
                "createdAt", "createdAt",
                "updatedAt", "updatedAt");
        String property = allowed.getOrDefault(filter.getSort() == null ? "" : filter.getSort(), fallbackSort);
        Sort.Direction direction = "asc".equalsIgnoreCase(filter.getDir()) ? Sort.Direction.ASC : Sort.Direction.DESC;
        int size = Math.min(Math.max(filter.getSize(), 5), 100);
        int page = Math.max(filter.getPage(), 0);
        return PageRequest.of(page, size, Sort.by(direction, property));
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private static String csvCell(String value) {
        if (value == null) {
            return "";
        }
        String cleaned = value.replace("\r", " ").replace("\n", " ");
        if (cleaned.contains(",") || cleaned.contains("\"") || cleaned.startsWith("=")
                || cleaned.startsWith("+") || cleaned.startsWith("-") || cleaned.startsWith("@")) {
            return "\"" + cleaned.replace("\"", "\"\"") + "\"";
        }
        return cleaned;
    }
}
