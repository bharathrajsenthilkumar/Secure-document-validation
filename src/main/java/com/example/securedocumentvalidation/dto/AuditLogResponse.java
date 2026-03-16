package com.example.securedocumentvalidation.dto;

import com.example.securedocumentvalidation.entity.AuditLog;

import java.time.LocalDateTime;

public class AuditLogResponse {

    private String username;
    private String action;
    private Long documentId;
    private LocalDateTime timestamp;

    public AuditLogResponse(AuditLog log) {
        this.username = log.getUsername();
        this.action = log.getAction().name();
        this.documentId = log.getDocumentId();
        this.timestamp = log.getTimestamp();
    }

    public String getUsername() { return username; }
    public String getAction() { return action; }
    public Long getDocumentId() { return documentId; }
    public LocalDateTime getTimestamp() { return timestamp; }
}
