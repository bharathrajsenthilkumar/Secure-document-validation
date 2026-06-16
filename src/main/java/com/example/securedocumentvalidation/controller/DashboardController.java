package com.example.securedocumentvalidation.controller;

import com.example.securedocumentvalidation.dto.DashboardStatsDTO;
import com.example.securedocumentvalidation.entity.AuditAction;
import com.example.securedocumentvalidation.repository.AuditLogRepository;
import com.example.securedocumentvalidation.repository.DocumentRepository;
import com.example.securedocumentvalidation.repository.UserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final UserRepository userRepository;
    private final DocumentRepository documentRepository;
    private final AuditLogRepository auditLogRepository;

    public DashboardController(
            UserRepository userRepository,
            DocumentRepository documentRepository,
            AuditLogRepository auditLogRepository) {

        this.userRepository = userRepository;
        this.documentRepository = documentRepository;
        this.auditLogRepository = auditLogRepository;
    }

    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public DashboardStatsDTO getStats() {

        return new DashboardStatsDTO(
                userRepository.count(),
                documentRepository.count(),
                auditLogRepository.countByAction(AuditAction.UPLOAD),
                auditLogRepository.countByAction(AuditAction.DOWNLOAD),
                auditLogRepository.countByAction(AuditAction.DENIED),
                auditLogRepository.countByAction(AuditAction.DELETE)
        );
    }
}