package com.sjc.schooldocuments.report;

import com.sjc.schooldocuments.common.CountRow;
import com.sjc.schooldocuments.request.ApprovalStatus;
import com.sjc.schooldocuments.request.DocumentRequestRepository;
import com.sjc.schooldocuments.request.RequestStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReportService {

    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final DocumentRequestRepository requestRepository;

    public ReportService(DocumentRequestRepository requestRepository) {
        this.requestRepository = requestRepository;
    }

    /**
     * Counting rules (stated once, applied everywhere):
     * every figure describes requests <em>submitted</em> within the selected
     * period, except "Awaiting approval now" and "Released", which describe
     * current state on the records submitted in that period.
     */
    @Transactional(readOnly = true)
    public ReportData build(ReportFilter filter) {
        LocalDate from = filter.getDateFrom() != null ? filter.getDateFrom() : LocalDate.now().minusDays(29);
        LocalDate to = filter.getDateTo() != null ? filter.getDateTo() : LocalDate.now();
        if (from.isAfter(to)) {
            LocalDate swap = from;
            from = to;
            to = swap;
        }
        LocalDateTime start = from.atStartOfDay();
        LocalDateTime end = to.atTime(23, 59, 59);

        long total = requestRepository.countByCreatedAtBetween(start, end);
        long pending = requestRepository.countByStatusAndCreatedAtBetween(RequestStatus.PENDING, start, end);
        long processing = requestRepository.countByStatusAndCreatedAtBetween(RequestStatus.PROCESSING, start, end);
        long ready = requestRepository.countByStatusAndCreatedAtBetween(RequestStatus.READY_FOR_PICKUP, start, end);
        long approved = requestRepository.countByApprovalStatusAndCreatedAtBetween(ApprovalStatus.APPROVED, start, end);
        long rejected = requestRepository.countByApprovalStatusAndCreatedAtBetween(ApprovalStatus.REJECTED, start, end);
        long awaitingApproval = requestRepository.countByApprovalStatusAndCreatedAtBetween(
                ApprovalStatus.PENDING, start, end);
        long released = requestRepository.countByClaimedAtIsNotNullAndCreatedAtBetween(start, end);

        return new ReportData(from, to, total, pending, processing, ready, approved, rejected,
                awaitingApproval, released,
                mapStatusCounts(requestRepository.countGroupedByStatus(start, end)),
                mapCounts(requestRepository.countGroupedByDocumentType(start, end), label -> label),
                mapMonthCounts(requestRepository.countGroupedByMonth(start, end)));
    }

    @Transactional(readOnly = true)
    public void exportCsv(PrintWriter writer, ReportData report) {
        writer.println("Section,Label,Count");
        writer.println("Summary,Total requests," + report.getTotalRequests());
        writer.println("Summary,Pending," + report.getPending());
        writer.println("Summary,Processing," + report.getProcessing());
        writer.println("Summary,Ready for pick-up," + report.getReady());
        writer.println("Summary,Approved," + report.getApproved());
        writer.println("Summary,Rejected," + report.getRejected());
        writer.println("Summary,Awaiting approval now," + report.getAwaitingApproval());
        writer.println("Summary,Released to student," + report.getReleased());
        for (CountRow row : report.getByStatus()) {
            writer.println("By status," + csv(row.getLabel()) + "," + row.getCount());
        }
        for (CountRow row : report.getByDocumentType()) {
            writer.println("By document type," + csv(row.getLabel()) + "," + row.getCount());
        }
        for (CountRow row : report.getMonthly()) {
            writer.println("Monthly volume," + csv(row.getLabel()) + "," + row.getCount());
        }
        writer.flush();
    }

    private List<CountRow> mapCounts(List<Object[]> rows, java.util.function.Function<String, String> labeller) {
        List<CountRow> result = new ArrayList<>();
        for (Object[] row : rows) {
            String label = labeller.apply(String.valueOf(row[0]));
            result.add(new CountRow(label, ((Number) row[1]).longValue()));
        }
        return result;
    }

    private List<CountRow> mapStatusCounts(List<Object[]> rows) {
        List<CountRow> result = new ArrayList<>();
        for (Object[] row : rows) {
            String raw = String.valueOf(row[0]);
            result.add(new CountRow(ReportLabels.statusLabel(raw), ((Number) row[1]).longValue(), raw));
        }
        return result;
    }

    private List<CountRow> mapMonthCounts(List<Object[]> rows) {
        List<CountRow> result = new ArrayList<>();
        for (Object[] row : rows) {
            java.time.YearMonth month = java.time.YearMonth.parse(String.valueOf(row[0]));
            result.add(new CountRow(month.getMonth().getDisplayName(java.time.format.TextStyle.SHORT, java.util.Locale.ENGLISH)
                    + " " + month.getYear(), ((Number) row[1]).longValue()));
        }
        return result;
    }

    private static String csv(String value) {
        if (value == null) {
            return "";
        }
        return value.contains(",") ? "\"" + value.replace("\"", "\"\"") + "\"" : value;
    }

    /**
     * Reuses the same display labels as the rest of the interface.
     */
    static final class ReportLabels {
        private ReportLabels() {
        }

        static String statusLabel(String raw) {
            try {
                return RequestStatus.valueOf(raw).getLabel();
            } catch (IllegalArgumentException ex) {
                return raw;
            }
        }
    }
}
