package com.sjc.schooldocuments.request.dto;

import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * Supplementary record of a physical hand-over. This never changes the
 * student-facing status.
 */
public class ReleaseRecordForm {

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate claimedOn;

    @Size(max = 500, message = "Remarks must be 500 characters or fewer.")
    private String remarks;

    public LocalDate getClaimedOn() {
        return claimedOn;
    }

    public void setClaimedOn(LocalDate claimedOn) {
        this.claimedOn = claimedOn;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
