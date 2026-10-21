package com.sjc.schooldocuments.report;

import com.sjc.schooldocuments.common.CountRow;

import java.time.LocalDate;
import java.util.List;

public class ReportData {

    private final LocalDate dateFrom;
    private final LocalDate dateTo;
    private final long totalRequests;
    private final long pending;
    private final long processing;
    private final long ready;
    private final long approved;
    private final long rejected;
    private final long awaitingApproval;
    private final long released;
    private final List<CountRow> byStatus;
    private final List<CountRow> byDocumentType;
    private final List<CountRow> monthly;

    @SuppressWarnings("java:S107")
    public ReportData(LocalDate dateFrom, LocalDate dateTo, long totalRequests, long pending, long processing,
                      long ready, long approved, long rejected, long awaitingApproval, long released,
                      List<CountRow> byStatus, List<CountRow> byDocumentType, List<CountRow> monthly) {
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.totalRequests = totalRequests;
        this.pending = pending;
        this.processing = processing;
        this.ready = ready;
        this.approved = approved;
        this.rejected = rejected;
        this.awaitingApproval = awaitingApproval;
        this.released = released;
        this.byStatus = byStatus;
        this.byDocumentType = byDocumentType;
        this.monthly = monthly;
    }

    public LocalDate getDateFrom() {
        return dateFrom;
    }

    public LocalDate getDateTo() {
        return dateTo;
    }

    public long getTotalRequests() {
        return totalRequests;
    }

    public long getPending() {
        return pending;
    }

    public long getProcessing() {
        return processing;
    }

    public long getReady() {
        return ready;
    }

    public long getApproved() {
        return approved;
    }

    public long getRejected() {
        return rejected;
    }

    public long getAwaitingApproval() {
        return awaitingApproval;
    }

    public long getReleased() {
        return released;
    }

    public List<CountRow> getByStatus() {
        return byStatus;
    }

    public List<CountRow> getByDocumentType() {
        return byDocumentType;
    }

    public List<CountRow> getMonthly() {
        return monthly;
    }
}
