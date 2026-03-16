package com.example.securedocumentvalidation.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_log")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, updatable = false)
    private String username;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private AuditAction action;

    @Column(nullable = false, updatable = false)
    private Long documentId;

    @Column(nullable = false, updatable = false)
    private LocalDateTime timestamp;

    protected AuditLog() {
        // JPA only
    }

    public AuditLog(String username, AuditAction action, Long documentId) {
        this.username = username;
        this.action = action;
        this.documentId = documentId;
        this.timestamp = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public AuditAction getAction() { return action; }
    public Long getDocumentId() { return documentId; }
    public LocalDateTime getTimestamp() { return timestamp; }
}
