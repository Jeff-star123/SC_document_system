package com.sjc.schooldocuments.history;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RequestStatusHistoryRepository extends JpaRepository<RequestStatusHistory, Long> {

    List<RequestStatusHistory> findByRequestIdOrderByCreatedAtAsc(Long requestId);
}
