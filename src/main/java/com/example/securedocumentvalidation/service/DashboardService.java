package com.example.securedocumentvalidation.service;

import com.example.securedocumentvalidation.dto.DashboardStatsDTO;
import com.example.securedocumentvalidation.entity.AuditAction;
import com.example.securedocumentvalidation.repository.AuditLogRepository;
import com.example.securedocumentvalidation.repository.DocumentRepository;
import com.example.securedocumentvalidation.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    private final UserRepository userRepository;
    private final DocumentRepository documentRepository;
    private final AuditLogRepository auditRepository;

    public DashboardService(
            UserRepository userRepository,
            DocumentRepository documentRepository,
            AuditLogRepository auditRepository
    ) {
        this.userRepository = userRepository;
        this.documentRepository = documentRepository;
        this.auditRepository = auditRepository;
    }

    public DashboardStatsDTO getStats() {

        long users =
                userRepository.count();

        long documents =
                documentRepository.count();

        long uploads =
                auditRepository.countByAction(
                        AuditAction.UPLOAD
                );

        long downloads =
                auditRepository.countByAction(
                        AuditAction.DOWNLOAD
                );

        long deletes =
                auditRepository.countByAction(
                        AuditAction.DELETE
                );

        long verifies =
                auditRepository.countByAction(
                        AuditAction.VERIFY
                );

        return new DashboardStatsDTO(
                users,
                documents,
                uploads,
                downloads,
                verifies,
                deletes
        );
    }
}