package com.sjc.schooldocuments.request;

/**
 * Supplementary approval state for document types that require administrative review.
 * Never replaces the core {@link RequestStatus} workflow.
 */
public enum ApprovalStatus {
    NOT_REQUIRED("No approval needed"),
    PENDING("Awaiting approval"),
    APPROVED("Approved"),
    REJECTED("Rejected");

    private final String label;

    ApprovalStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
