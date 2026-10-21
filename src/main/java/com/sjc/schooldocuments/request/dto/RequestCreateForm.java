package com.sjc.schooldocuments.request.dto;

import jakarta.validation.constraints.*;

/**
 * Student submission form. The student identity is taken from the authenticated
 * account, never from the form.
 */
public class RequestCreateForm {

    @NotNull(message = "Choose the document you are requesting.")
    private Long documentTypeId;

    @NotBlank(message = "Tell the registrar why you need this document.")
    @Size(max = 500, message = "Purpose must be 500 characters or fewer.")
    private String purpose;

    @Min(value = 1, message = "Quantity must be at least 1.")
    @Max(value = 5, message = "A single request can cover up to 5 copies. Submit a second request for more.")
    private int quantity = 1;

    @Size(max = 2000, message = "Additional details must be 2000 characters or fewer.")
    private String additionalDetails;

    @AssertTrue(message = "Confirm that the information you submitted is accurate.")
    private boolean confirmAccurate;

    public Long getDocumentTypeId() {
        return documentTypeId;
    }

    public void setDocumentTypeId(Long documentTypeId) {
        this.documentTypeId = documentTypeId;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getAdditionalDetails() {
        return additionalDetails;
    }

    public void setAdditionalDetails(String additionalDetails) {
        this.additionalDetails = additionalDetails;
    }

    public boolean isConfirmAccurate() {
        return confirmAccurate;
    }

    public void setConfirmAccurate(boolean confirmAccurate) {
        this.confirmAccurate = confirmAccurate;
    }
}
