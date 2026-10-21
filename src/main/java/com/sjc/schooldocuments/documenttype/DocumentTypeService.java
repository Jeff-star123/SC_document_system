package com.sjc.schooldocuments.documenttype;

import com.sjc.schooldocuments.audit.AuditService;
import com.sjc.schooldocuments.common.exception.BusinessRuleException;
import com.sjc.schooldocuments.common.exception.ResourceNotFoundException;
import com.sjc.schooldocuments.documenttype.dto.DocumentTypeForm;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DocumentTypeService {

    private final DocumentTypeRepository repository;
    private final AuditService auditService;
    private final com.sjc.schooldocuments.request.DocumentRequestRepository requestRepository;

    public DocumentTypeService(DocumentTypeRepository repository, AuditService auditService,
                               com.sjc.schooldocuments.request.DocumentRequestRepository requestRepository) {
        this.repository = repository;
        this.auditService = auditService;
        this.requestRepository = requestRepository;
    }

    @Transactional(readOnly = true)
    public List<DocumentType> active() {
        return repository.findByActiveTrueOrderByNameAsc();
    }

    @Transactional(readOnly = true)
    public List<DocumentType> all() {
        return repository.findAllByOrderByNameAsc();
    }

    @Transactional(readOnly = true)
    public DocumentType require(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("That document type could not be found."));
    }

    @Transactional(readOnly = true)
    public DocumentTypeForm toForm(Long id) {
        DocumentType type = require(id);
        DocumentTypeForm form = new DocumentTypeForm();
        form.setId(type.getId());
        form.setName(type.getName());
        form.setDescription(type.getDescription());
        form.setActive(type.isActive());
        form.setRequiresAdminApproval(type.isRequiresAdminApproval());
        form.setInstructions(type.getInstructions());
        form.setInternalNotes(type.getInternalNotes());
        return form;
    }

    @Transactional
    public void save(DocumentTypeForm form) {
        String name = form.getName().trim();
        repository.findByNameIgnoreCase(name).ifPresent(existing -> {
            if (form.getId() == null || !existing.getId().equals(form.getId())) {
                throw new BusinessRuleException("A document type named \"" + name + "\" already exists.");
            }
        });

        DocumentType type = form.getId() == null ? new DocumentType() : require(form.getId());
        boolean creating = form.getId() == null;
        type.setName(name);
        type.setDescription(blankToNull(form.getDescription()));
        type.setActive(form.isActive());
        type.setRequiresAdminApproval(form.isRequiresAdminApproval());
        type.setInstructions(blankToNull(form.getInstructions()));
        type.setInternalNotes(blankToNull(form.getInternalNotes()));
        DocumentType saved = repository.save(type);

        auditService.record(creating ? "DOCUMENT_TYPE_CREATED" : "DOCUMENT_TYPE_UPDATED",
                "DocumentType", saved.getId(),
                (creating ? "Created" : "Updated") + " document type \"" + saved.getName() + "\""
                        + (saved.isRequiresAdminApproval() ? " (requires administrator approval)" : ""));
    }

    @Transactional
    public void setActive(Long id, boolean active) {
        DocumentType type = require(id);
        if (!active) {
            long outstanding = requestRepository.countByDocumentTypeIdAndStatusNot(
                    id, com.sjc.schooldocuments.request.RequestStatus.READY_FOR_PICKUP);
            if (outstanding > 0) {
                throw new BusinessRuleException("This document type is used by " + outstanding
                        + " requests still in progress. Finish or reassign them before disabling it.");
            }
        }
        type.setActive(active);
        repository.save(type);
        auditService.record(active ? "DOCUMENT_TYPE_ENABLED" : "DOCUMENT_TYPE_DISABLED", "DocumentType", id,
                "Document type \"" + type.getName() + "\" " + (active ? "enabled" : "disabled"));
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
