package com.example.securedocumentvalidation.security;

import com.example.securedocumentvalidation.entity.AuditAction;
import com.example.securedocumentvalidation.entity.SharedDocument;
import com.example.securedocumentvalidation.repository.SharedDocumentRepository;
import com.example.securedocumentvalidation.service.AuditService;
import org.springframework.stereotype.Service;

@Service
public class SharedDocumentService {

    private final SharedDocumentRepository repository;
    private final AuditService auditService;

    public SharedDocumentService(
            SharedDocumentRepository repository,
            AuditService auditService
    ) {
        this.repository = repository;
        this.auditService = auditService;
    }

    public void shareDocument(
            Long documentId,
            String owner,
            String sharedUser
    ) {

        SharedDocument shared =
                new SharedDocument(
                        documentId,
                        owner,
                        sharedUser
                );
        if (repository.findByDocumentIdAndSharedWithUsername(
                documentId,
                sharedUser
        ).isPresent()) {

            throw new RuntimeException("Document already shared with this user.");
        }

        repository.save(shared);

        auditService.log(
                owner,
                AuditAction.SHARE,
                documentId
        );
    }
}
