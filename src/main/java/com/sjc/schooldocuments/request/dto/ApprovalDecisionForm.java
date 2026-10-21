package com.sjc.schooldocuments.request.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Administrative decision on a request that requires approval.
 */
public class ApprovalDecisionForm {

    private boolean approve;

    @Size(max = 1000, message = "Reason must be 1000 characters or fewer.")
    private String reason;

    @Size(max = 1000, message = "Remarks must be 1000 characters or fewer.")
    private String remarks;

    public boolean isApprove() {
        return approve;
    }

    public void setApprove(boolean approve) {
        this.approve = approve;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
