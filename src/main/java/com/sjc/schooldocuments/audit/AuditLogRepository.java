package com.sjc.schooldocuments.audit;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.List;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long>, JpaSpecificationExecutor<AuditLog> {

    Page<AuditLog> findAllByOrderByCreatedAtDesc(Pageable pageable);

    List<AuditLog> findTop8ByOrderByCreatedAtDesc();

    @org.springframework.data.jpa.repository.Query("select distinct a.action from AuditLog a order by a.action")
    List<String> distinctActions();
}
