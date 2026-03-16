package com.example.securedocumentvalidation.controller;

import com.example.securedocumentvalidation.dto.AuditLogResponse;
import com.example.securedocumentvalidation.entity.AuditAction;
import com.example.securedocumentvalidation.repository.AuditLogRepository;
import org.springframework.data.domain.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/audit")
public class AuditController {

    private final AuditLogRepository repository;

    public AuditController(AuditLogRepository repository) {
        this.repository = repository;
    }

    private Pageable buildPageable(int page, int size) {
        return PageRequest.of(
                page,
                size,
                Sort.by("timestamp").descending()
        );
    }

    // ===============================
    // Get All Logs (ADMIN only)
    // ===============================
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Page<AuditLogResponse> getAllLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return repository.findAll(buildPageable(page, size))
                .map(AuditLogResponse::new);
    }

    // ===============================
    // Filter by Username
    // ===============================
    @GetMapping("/user/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public Page<AuditLogResponse> getByUser(
            @PathVariable String username,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return repository.findByUsername(username, buildPageable(page, size))
                .map(AuditLogResponse::new);
    }

    // ===============================
    // Filter by Action
    // ===============================
    @GetMapping("/action/{action}")
    @PreAuthorize("hasRole('ADMIN')")
    public Page<AuditLogResponse> getByAction(
            @PathVariable AuditAction action,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return repository.findByAction(action, buildPageable(page, size))
                .map(AuditLogResponse::new);
    }
}
