package com.example.securedocumentvalidation.repository;

import com.example.securedocumentvalidation.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface DocumentRepository extends JpaRepository<Document, Long> {

    List<Document> findByOwnerUsername(String ownerUsername);

    Page<Document> findByOwnerUsername(String ownerUsername, Pageable pageable);
}
