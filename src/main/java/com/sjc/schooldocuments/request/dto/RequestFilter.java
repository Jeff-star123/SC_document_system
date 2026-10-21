package com.sjc.schooldocuments.request.dto;

import com.sjc.schooldocuments.request.RequestStatus;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Backing object for the request tables: search, filters, sorting and paging.
 * Values survive round-trips so a filtered page keeps its query in the URL.
 */
public class RequestFilter {

    private String q;
    private RequestStatus status;
    private Long documentTypeId;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateFrom;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateTo;

    private int page = 0;
    private int size = 10;
    private String sort = "createdAt";
    private String dir = "desc";
    private boolean awaitingApprovalOnly;

    public String getQ() {
        return q;
    }

    public void setQ(String q) {
        this.q = q;
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

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public boolean isAwaitingApprovalOnly() {
        return awaitingApprovalOnly;
    }

    public void setAwaitingApprovalOnly(boolean awaitingApprovalOnly) {
        this.awaitingApprovalOnly = awaitingApprovalOnly;
    }

    public boolean hasFilters() {
        return (q != null && !q.isBlank())
                || status != null
                || documentTypeId != null
                || dateFrom != null
                || dateTo != null
                || awaitingApprovalOnly;
    }

    /**
     * Query string fragments for pagination and sorting links, preserving
     * whatever filters are currently applied.
     */
    public String toQueryString() {
        return build(new LinkedHashMap<>());
    }

    /**
     * Link that sorts by the given column, flipping direction when the column
     * is already the active sort. Any change resets to the first page.
     */
    public String sortLink(String field) {
        var overrides = new LinkedHashMap<String, String>();
        overrides.put("sort", field);
        overrides.put("dir", field.equals(sort) ? ("asc".equalsIgnoreCase(dir) ? "desc" : "asc") : "asc");
        return build(overrides);
    }

    public String queryWithPage(int newPage) {
        var overrides = new LinkedHashMap<String, String>();
        overrides.put("page", String.valueOf(newPage));
        return build(overrides);
    }

    public boolean isSortedBy(String field) {
        return field.equals(sort);
    }

    public boolean isSortAscending() {
        return "asc".equalsIgnoreCase(dir);
    }

    private String build(Map<String, String> overrides) {
        var params = new LinkedHashMap<String, String>();
        if (q != null && !q.isBlank()) {
            params.put("q", q);
        }
        if (status != null) {
            params.put("status", status.name());
        }
        if (documentTypeId != null) {
            params.put("documentTypeId", String.valueOf(documentTypeId));
        }
        if (dateFrom != null) {
            params.put("dateFrom", dateFrom.toString());
        }
        if (dateTo != null) {
            params.put("dateTo", dateTo.toString());
        }
        if (awaitingApprovalOnly) {
            params.put("awaitingApprovalOnly", "true");
        }
        params.put("size", String.valueOf(size));
        if (sort != null && !sort.isBlank()) {
            params.put("sort", sort);
        }
        if (dir != null && !dir.isBlank()) {
            params.put("dir", dir);
        }
        params.put("page", String.valueOf(Math.max(0, page)));
        params.putAll(overrides);

        List<String> parts = new ArrayList<>();
        params.forEach((key, value) -> {
            if (value != null && !value.isBlank()) {
                parts.add(encode(key) + "=" + encode(value));
            }
        });
        return String.join("&", parts);
    }

    private static String encode(String value) {
        return java.net.URLEncoder.encode(value, java.nio.charset.StandardCharsets.UTF_8);
    }
}
