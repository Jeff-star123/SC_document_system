package com.sjc.schooldocuments.admin;

import com.sjc.schooldocuments.audit.dto.AuditRowDto;
import com.sjc.schooldocuments.common.CountRow;

import java.util.List;

public class AdminDashboardData {

    private final long totalUsers;
    private final long students;
    private final long staff;
    private final long admins;
    private final long totalRequests;
    private final long pending;
    private final long processing;
    private final long ready;
    private final long approved;
    private final long rejected;
    private final long awaitingApproval;
    private final long releasedThisMonth;
    private final List<CountRow> requestsByStatus;
    private final List<CountRow> requestsByDocumentType;
    private final List<CountRow> monthlyVolume;
    private final List<AuditRowDto> recentActivity;

    @SuppressWarnings("java:S107")
    public AdminDashboardData(long totalUsers, long students, long staff, long admins, long totalRequests,
                              long pending, long processing, long ready, long approved, long rejected,
                              long awaitingApproval, long releasedThisMonth, List<CountRow> requestsByStatus,
                              List<CountRow> requestsByDocumentType, List<CountRow> monthlyVolume,
                              List<AuditRowDto> recentActivity) {
        this.totalUsers = totalUsers;
        this.students = students;
        this.staff = staff;
        this.admins = admins;
        this.totalRequests = totalRequests;
        this.pending = pending;
        this.processing = processing;
        this.ready = ready;
        this.approved = approved;
        this.rejected = rejected;
        this.awaitingApproval = awaitingApproval;
        this.releasedThisMonth = releasedThisMonth;
        this.requestsByStatus = requestsByStatus;
        this.requestsByDocumentType = requestsByDocumentType;
        this.monthlyVolume = monthlyVolume;
        this.recentActivity = recentActivity;
    }

    public long getTotalUsers() {
        return totalUsers;
    }

    public long getStudents() {
        return students;
    }

    public long getStaff() {
        return staff;
    }

    public long getAdmins() {
        return admins;
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

    public long getReleasedThisMonth() {
        return releasedThisMonth;
    }

    public List<CountRow> getRequestsByStatus() {
        return requestsByStatus;
    }

    public List<CountRow> getRequestsByDocumentType() {
        return requestsByDocumentType;
    }

    public List<CountRow> getMonthlyVolume() {
        return monthlyVolume;
    }

    public List<AuditRowDto> getRecentActivity() {
        return recentActivity;
    }
}
