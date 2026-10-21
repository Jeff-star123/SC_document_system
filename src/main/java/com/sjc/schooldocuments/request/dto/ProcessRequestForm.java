package com.sjc.schooldocuments.request.dto;

import jakarta.validation.constraints.Size;

/**
 * Registrar starts processing a verified request.
 */
public class ProcessRequestForm {

    @Size(max = 1000, message = "Remarks must be 1000 characters or fewer.")
    private String remarks;

    @Size(max = 2000, message = "Internal notes must be 2000 characters or fewer.")
    private String staffNotes;

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getStaffNotes() {
        return staffNotes;
    }

    public void setStaffNotes(String staffNotes) {
        this.staffNotes = staffNotes;
    }
}
