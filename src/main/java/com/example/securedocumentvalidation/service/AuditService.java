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

    public void log(String username, AuditAction action, Long documentId) {
        AuditLog log = new AuditLog(username, action, documentId);
        repository.save(log);
    }
}
