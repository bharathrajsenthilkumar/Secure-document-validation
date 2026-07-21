package com.example.securedocumentvalidation.service;

import com.example.securedocumentvalidation.entity.AuditAction;
import com.example.securedocumentvalidation.entity.AuditLog;
import com.example.securedocumentvalidation.repository.AuditLogRepository;
import org.springframework.stereotype.Service;

@Service
public class AuditService {

    private final AuditLogRepository repository;

    public AuditService(AuditLogRepository repository) {
        this.repository = repository;
    }

    // =========================
    // SAVE AUDIT LOG
    // =========================
    public void log(
            String username,
            AuditAction action,
            Long documentId
    ) {
        AuditLog log =
                new AuditLog(
                        username,
                        action,
                        documentId
                );

        repository.save(log);
    }

    // =========================
    // DASHBOARD STATISTICS
    // =========================
    public long getUploadCount() {
        return repository.countByAction(
                AuditAction.UPLOAD
        );
    }

    public long getDownloadCount() {
        return repository.countByAction(
                AuditAction.DOWNLOAD
        );
    }

    public long getDeleteCount() {
        return repository.countByAction(
                AuditAction.DELETE
        );
    }

    public long getVerifyCount() {
        return repository.countByAction(
                AuditAction.VERIFY
        );
    }
}