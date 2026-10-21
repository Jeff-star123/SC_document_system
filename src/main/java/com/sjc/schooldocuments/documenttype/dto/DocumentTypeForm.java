package com.sjc.schooldocuments.documenttype.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class DocumentTypeForm {

    private Long id;

    @NotBlank(message = "Give the document a name students will recognise.")
    @Size(max = 150, message = "Name must be 150 characters or fewer.")
    private String name;

    @Size(max = 500, message = "Description must be 500 characters or fewer.")
    private String description;

    private boolean active = true;

    private boolean requiresAdminApproval;

    @Size(max = 1000, message = "Instructions must be 1000 characters or fewer.")
    private String instructions;

    @Size(max = 1000, message = "Internal notes must be 1000 characters or fewer.")
    private String internalNotes;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isRequiresAdminApproval() {
        return requiresAdminApproval;
    }

    public void setRequiresAdminApproval(boolean requiresAdminApproval) {
        this.requiresAdminApproval = requiresAdminApproval;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public String getInternalNotes() {
        return internalNotes;
    }

    public void setInternalNotes(String internalNotes) {
        this.internalNotes = internalNotes;
    }
}
