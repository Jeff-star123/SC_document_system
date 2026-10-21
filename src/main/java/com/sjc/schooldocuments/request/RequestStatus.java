package com.sjc.schooldocuments.request;

/**
 * The three student-facing statuses of a document request.
 */
public enum RequestStatus {
    PENDING("Pending"),
    PROCESSING("Processing"),
    READY_FOR_PICKUP("Ready for Pick-up");

    private final String label;

    RequestStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
