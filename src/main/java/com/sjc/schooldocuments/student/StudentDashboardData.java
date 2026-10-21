package com.sjc.schooldocuments.student;

import com.sjc.schooldocuments.notification.dto.NotificationDto;
import com.sjc.schooldocuments.request.dto.RequestSummaryDto;

import java.util.List;

public class StudentDashboardData {

    private final long total;
    private final long pending;
    private final long processing;
    private final long ready;
    private final long unreadNotifications;
    private final List<RequestSummaryDto> recentRequests;
    private final List<NotificationDto> latestNotifications;

    public StudentDashboardData(long total, long pending, long processing, long ready, long unreadNotifications,
                                List<RequestSummaryDto> recentRequests, List<NotificationDto> latestNotifications) {
        this.total = total;
        this.pending = pending;
        this.processing = processing;
        this.ready = ready;
        this.unreadNotifications = unreadNotifications;
        this.recentRequests = recentRequests;
        this.latestNotifications = latestNotifications;
    }

    public long getTotal() {
        return total;
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

    public long getUnreadNotifications() {
        return unreadNotifications;
    }

    public List<RequestSummaryDto> getRecentRequests() {
        return recentRequests;
    }

    public List<NotificationDto> getLatestNotifications() {
        return latestNotifications;
    }
}
