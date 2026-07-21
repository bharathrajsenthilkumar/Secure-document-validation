package com.example.securedocumentvalidation.controller;

import com.example.securedocumentvalidation.dto.AuditLogResponse;
import com.example.securedocumentvalidation.entity.AuditAction;
import com.example.securedocumentvalidation.repository.AuditLogRepository;
import com.example.securedocumentvalidation.service.AuditExportService;
import com.example.securedocumentvalidation.dto.DashboardStatsDTO;
import com.example.securedocumentvalidation.service.AuditService;
import com.example.securedocumentvalidation.repository.DocumentRepository;
import com.example.securedocumentvalidation.repository.UserRepository;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;

@RestController
@RequestMapping("/api/audit")
public class AuditController {

    private final AuditLogRepository repository;
    private final AuditExportService exportService;
    private final AuditService auditService;
    private final UserRepository userRepository;
    private final DocumentRepository documentRepository;

    public AuditController(
            AuditLogRepository repository,
            AuditExportService exportService,
            AuditService auditService,
            UserRepository userRepository,
            DocumentRepository documentRepository
    ) {
        this.repository = repository;
        this.exportService = exportService;
        this.auditService = auditService;
        this.userRepository = userRepository;
        this.documentRepository =
                documentRepository;
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
    // ===============================
// Dashboard Statistics
// ===============================
    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public DashboardStatsDTO
    getDashboardStats() {

        long totalUsers =
                userRepository.count();

        long totalDocuments =
                documentRepository.count();

        long totalUploads =
                auditService.getUploadCount();

        long totalDownloads =
                auditService.getDownloadCount();

        long totalDeletedDocuments =
                auditService.getDeleteCount();

        long totalDeniedAccess = 0;

        return new DashboardStatsDTO(
                totalUsers,
                totalDocuments,
                totalUploads,
                totalDownloads,
                totalDeniedAccess,
                totalDeletedDocuments
        );
    }
    @GetMapping("/export/excel")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Resource> exportExcel()
            throws Exception {

        ByteArrayInputStream stream =
                exportService.exportToExcel();

        InputStreamResource file =
                new InputStreamResource(stream);

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=audit_logs.xlsx"
                )
                .contentType(
                        MediaType.APPLICATION_OCTET_STREAM
                )
                .body(file);
    }
    @GetMapping("/export/pdf")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Resource> exportPdf()
            throws Exception {

        ByteArrayInputStream stream =
                exportService.exportToPdf();

        InputStreamResource file =
                new InputStreamResource(stream);

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=audit_logs.pdf"
                )
                .contentType(
                        MediaType.APPLICATION_PDF
                )
                .body(file);
    }
}
