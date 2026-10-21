package com.sjc.schooldocuments.report;

import com.sjc.schooldocuments.request.RequestStatus;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public class ReportFilter {

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateFrom;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateTo;

    private RequestStatus status;

    private Long documentTypeId;

    public LocalDate getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(LocalDate dateFrom) {
        this.dateFrom = dateFrom;
    }

    public LocalDate getDateTo() {
        return dateTo;
    }

    public void setDateTo(LocalDate dateTo) {
        this.dateTo = dateTo;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    public Long getDocumentTypeId() {
        return documentTypeId;
    }

    public void setDocumentTypeId(Long documentTypeId) {
        this.documentTypeId = documentTypeId;
    }

    public String toQueryString() {
        StringBuilder sb = new StringBuilder();
        if (dateFrom != null) {
            sb.append("&dateFrom=").append(dateFrom);
        }
        if (dateTo != null) {
            sb.append("&dateTo=").append(dateTo);
        }
        if (status != null) {
            sb.append("&status=").append(status.name());
        }
        if (documentTypeId != null) {
            sb.append("&documentTypeId=").append(documentTypeId);
        }
        return sb.toString();
    }
}
