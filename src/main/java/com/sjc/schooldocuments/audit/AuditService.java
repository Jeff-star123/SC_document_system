package com.sjc.schooldocuments.audit;

import com.sjc.schooldocuments.audit.dto.AuditRowDto;
import com.sjc.schooldocuments.common.CurrentUserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Writes the append-only audit trail. Descriptions never contain passwords,
 * tokens, or other secrets.
 */
@Service
public class AuditService {

    private final AuditLogRepository repository;
    private final CurrentUserService currentUserService;

    public AuditService(AuditLogRepository repository, CurrentUserService currentUserService) {
        this.repository = repository;
        this.currentUserService = currentUserService;
    }

    /**
     * Joined to the caller's transaction so an audited action and its log entry
     * succeed or fail together.
     */
    @Transactional(propagation = Propagation.MANDATORY)
    public void record(String action, String entityType, Long entityId, String description) {
        AuditLog log = new AuditLog();
        log.setActor(currentUserService.current().orElse(null));
        log.setAction(action);
        log.setEntityType(entityType);
        log.setEntityId(entityId);
        log.setDescription(description);
        repository.save(log);
    }

    @Transactional
    public void recordStandalone(String action, String entityType, Long entityId, String description) {
        record(action, entityType, entityId, description);
    }

    @Transactional(readOnly = true)
    public List<AuditRowDto> recent(int limit) {
        Pageable pageable = PageRequest.of(0, Math.max(1, limit), Sort.by(Sort.Direction.DESC, "createdAt"));
        return repository.findAll(pageable).map(AuditRowDto::from).getContent();
    }

    @Transactional(readOnly = true)
    public Page<AuditRowDto> search(String action, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(0, page), Math.min(Math.max(1, size), 100),
                Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<AuditLog> result = (action == null || action.isBlank())
                ? repository.findAll(pageable)
                : repository.findAll((root, query, cb) -> cb.equal(root.get("action"), action), pageable);
        return result.map(AuditRowDto::from);
    }

    @Transactional(readOnly = true)
    public List<String> distinctActions() {
        return repository.distinctActions();
    }
}
