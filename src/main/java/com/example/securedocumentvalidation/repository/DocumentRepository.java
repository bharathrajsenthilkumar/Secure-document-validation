package com.example.securedocumentvalidation.repository;

import com.example.securedocumentvalidation.entity.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface DocumentRepository extends JpaRepository<Document, Long> {

    List<Document> findByOwnerUsername(String ownerUsername);

    Page<Document> findByOwnerUsername(String ownerUsername, Pageable pageable);

    Page<Document> findByFilenameContainingIgnoreCase(
            String filename,
            Pageable pageable
    );

    Page<Document> findByOwnerUsernameContainingIgnoreCase(
            String ownerUsername,
            Pageable pageable
    );

    Page<Document> findByFilenameContainingIgnoreCaseAndOwnerUsername(
            String filename,
            String ownerUsername,
            Pageable pageable
    );

    Optional<Document> findFirstByFilenameAndOwnerUsernameOrderByVersionDesc(
            String filename,
            String ownerUsername
    );

    List<Document> findByDocumentGroupIdOrderByVersionDesc(
            String documentGroupId
    );

    @Query("""
    SELECT DISTINCT d
    FROM Document d
    WHERE d.ownerUsername = :username
       OR d.id IN (
            SELECT s.documentId
            FROM SharedDocument s
            WHERE s.sharedWithUsername = :username
       )
    """)
    Page<Document> findUserAndSharedDocuments(
            @Param("username") String username,
            Pageable pageable
    );
}