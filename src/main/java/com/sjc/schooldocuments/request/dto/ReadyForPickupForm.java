package com.sjc.schooldocuments.request.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Registrar marks a prepared document as ready for pick-up. Pick-up
 * instructions are required: the student notification carries them verbatim.
 */
public class ReadyForPickupForm {

    @NotBlank(message = "Give the student pick-up instructions before marking this ready.")
    @Size(max = 2000, message = "Pick-up instructions must be 2000 characters or fewer.")
    private String pickupInstructions;

    @Size(max = 2000, message = "Internal notes must be 2000 characters or fewer.")
    private String staffNotes;

    public String getPickupInstructions() {
        return pickupInstructions;
    }

    public void setPickupInstructions(String pickupInstructions) {
        this.pickupInstructions = pickupInstructions;
    }

    public String getStaffNotes() {
        return staffNotes;
    }

    public void setStaffNotes(String staffNotes) {
        this.staffNotes = staffNotes;
    }
}
