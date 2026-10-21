package com.sjc.schooldocuments.admin;

import com.sjc.schooldocuments.audit.AuditService;
import com.sjc.schooldocuments.common.CountRow;
import com.sjc.schooldocuments.documenttype.DocumentTypeRepository;
import com.sjc.schooldocuments.request.ApprovalStatus;
import com.sjc.schooldocuments.request.DocumentRequestRepository;
import com.sjc.schooldocuments.request.RequestStatus;
import com.sjc.schooldocuments.user.AccountStatus;
import com.sjc.schooldocuments.user.Role;
import com.sjc.schooldocuments.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class AdminService {

    private static final int VOLUME_MONTHS = 6;

    private final UserRepository userRepository;
    private final DocumentRequestRepository requestRepository;
    private final DocumentTypeRepository documentTypeRepository;
    private final AuditService auditService;

    public AdminService(UserRepository userRepository, DocumentRequestRepository requestRepository,
                        DocumentTypeRepository documentTypeRepository, AuditService auditService) {
        this.userRepository = userRepository;
        this.requestRepository = requestRepository;
        this.documentTypeRepository = documentTypeRepository;
        this.auditService = auditService;
    }

    @Transactional(readOnly = true)
    public AdminDashboardData dashboard() {
        LocalDateTime startOfMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime volumeStart = YearMonth.from(LocalDate.now()).minusMonths(VOLUME_MONTHS - 1)
                .atDay(1).atStartOfDay();

        return new AdminDashboardData(
                userRepository.count(),
                userRepository.countByRole(Role.STUDENT),
                userRepository.countByRole(Role.STAFF),
                userRepository.countByRole(Role.ADMIN),
                requestRepository.count(),
                requestRepository.countByStatus(RequestStatus.PENDING),
                requestRepository.countByStatus(RequestStatus.PROCESSING),
                requestRepository.countByStatus(RequestStatus.READY_FOR_PICKUP),
                requestRepository.countByApprovalStatus(ApprovalStatus.APPROVED),
                requestRepository.countByApprovalStatus(ApprovalStatus.REJECTED),
                requestRepository.countAwaitingApproval(),
                requestRepository.countByClaimedAtBetween(startOfMonth, now),
                statusRows(volumeStart, now),
                documentTypeRows(volumeStart, now),
                monthRows(volumeStart, now),
                auditService.recent(8));
    }

    private List<CountRow> statusRows(LocalDateTime from, LocalDateTime to) {
        List<CountRow> rows = new ArrayList<>();
        for (Object[] row : requestRepository.countGroupedByStatus(from, to)) {
            RequestStatus status = RequestStatus.valueOf(String.valueOf(row[0]));
            rows.add(new CountRow(status.getLabel(), ((Number) row[1]).longValue(), status.name()));
        }
        return rows;
    }

    private List<CountRow> documentTypeRows(LocalDateTime from, LocalDateTime to) {
        List<CountRow> rows = new ArrayList<>();
        for (Object[] row : requestRepository.countGroupedByDocumentType(from, to)) {
            rows.add(new CountRow(String.valueOf(row[0]), ((Number) row[1]).longValue()));
        }
        return rows;
    }

    private List<CountRow> monthRows(LocalDateTime from, LocalDateTime to) {
        List<CountRow> rows = new ArrayList<>();
        for (Object[] row : requestRepository.countGroupedByMonth(from, to)) {
            YearMonth month = YearMonth.parse(String.valueOf(row[0]));
            rows.add(new CountRow(
                    month.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH) + " " + month.getYear(),
                    ((Number) row[1]).longValue()));
        }
        return rows;
    }

    @Transactional(readOnly = true)
    public long activeDocumentTypeCount() {
        return documentTypeRepository.countByActiveTrue();
    }

    @Transactional(readOnly = true)
    public long inactiveAccountCount() {
        return userRepository.countByAccountStatus(AccountStatus.INACTIVE);
    }
}
