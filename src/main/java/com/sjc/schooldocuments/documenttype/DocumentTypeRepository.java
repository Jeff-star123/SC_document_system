package com.sjc.schooldocuments.documenttype;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface DocumentTypeRepository extends JpaRepository<DocumentType, Long> {

    List<DocumentType> findByActiveTrueOrderByNameAsc();

    List<DocumentType> findAllByOrderByNameAsc();

    Optional<DocumentType> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);

    long countByActiveTrue();
}
