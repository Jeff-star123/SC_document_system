package com.sjc.schooldocuments.staff;

import com.sjc.schooldocuments.request.dto.RequestSummaryDto;

import java.util.List;

public class StaffDashboardData {

    private final long active;
    private final long pending;
    private final long processing;
    private final long ready;
    private final long awaitingApproval;
    private final long readyToProcess;
    private final long releasedThisMonth;
    private final List<RequestSummaryDto> recentRequests;
    private final List<RequestSummaryDto> recentlyUpdated;

    @SuppressWarnings("java:S107")
    public StaffDashboardData(long active, long pending, long processing, long ready, long awaitingApproval,
                              long readyToProcess, long releasedThisMonth, List<RequestSummaryDto> recentRequests,
                              List<RequestSummaryDto> recentlyUpdated) {
        this.active = active;
        this.pending = pending;
        this.processing = processing;
        this.ready = ready;
        this.awaitingApproval = awaitingApproval;
        this.readyToProcess = readyToProcess;
        this.releasedThisMonth = releasedThisMonth;
        this.recentRequests = recentRequests;
        this.recentlyUpdated = recentlyUpdated;
    }

    public long getActive() {
        return active;
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

    public long getAwaitingApproval() {
        return awaitingApproval;
    }

    public long getReadyToProcess() {
        return readyToProcess;
    }

    public long getReleasedThisMonth() {
        return releasedThisMonth;
    }

    public List<RequestSummaryDto> getRecentRequests() {
        return recentRequests;
    }

    public List<RequestSummaryDto> getRecentlyUpdated() {
        return recentlyUpdated;
    }
}
