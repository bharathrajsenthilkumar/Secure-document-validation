package com.example.securedocumentvalidation.repository;

import com.example.securedocumentvalidation.entity.AuditLog;
import com.example.securedocumentvalidation.entity.AuditAction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    Page<AuditLog> findByUsername(String username, Pageable pageable);

    Page<AuditLog> findByAction(AuditAction action, Pageable pageable);
}
