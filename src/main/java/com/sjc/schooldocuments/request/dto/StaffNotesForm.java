package com.sjc.schooldocuments.request.dto;

import jakarta.validation.constraints.Size;

/**
 * Registrar edits internal notes without changing status.
 */
public class StaffNotesForm {

    @Size(max = 2000, message = "Internal notes must be 2000 characters or fewer.")
    private String staffNotes;

    public String getStaffNotes() {
        return staffNotes;
    }

    public void setStaffNotes(String staffNotes) {
        this.staffNotes = staffNotes;
    }
}
