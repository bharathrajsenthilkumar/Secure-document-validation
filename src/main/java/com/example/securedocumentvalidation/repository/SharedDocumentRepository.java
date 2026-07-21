package com.example.securedocumentvalidation.repository;

import com.example.securedocumentvalidation.entity.SharedDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SharedDocumentRepository
        extends JpaRepository<SharedDocument, Long> {

    Optional<SharedDocument>
    findByDocumentIdAndSharedWithUsername(
            Long documentId,
            String username
    );

    List<SharedDocument>
    findBySharedWithUsername(
            String username
    );
}