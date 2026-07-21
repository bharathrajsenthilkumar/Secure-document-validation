package com.example.securedocumentvalidation.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "documents")
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String filename;

    private String filepath;

    private String ownerUsername;

    private LocalDateTime uploadedAt = LocalDateTime.now();

    // =========================
    // Version History
    // =========================
    @Column(nullable = false)
    private String documentGroupId;

    @Column(nullable = false)
    private Integer version;

    @Column(nullable = false)
    private Boolean latest;

    // =========================
    // Integrity Hash
    // =========================
    @Column(nullable = false, length = 64)
    private String hash;

    // =========================
    // Getters
    // =========================

    public Long getId() {
        return id;
    }

    public String getFilename() {
        return filename;
    }

    public String getFilepath() {
        return filepath;
    }

    public String getOwnerUsername() {
        return ownerUsername;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public String getDocumentGroupId() {
        return documentGroupId;
    }

    public Integer getVersion() {
        return version;
    }

    public Boolean getLatest() {
        return latest;
    }

    public String getHash() {
        return hash;
    }

    // =========================
    // Setters
    // =========================

    public void setId(Long id) {
        this.id = id;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public void setOwnerUsername(String ownerUsername) {
        this.ownerUsername = ownerUsername;
    }

    public void setDocumentGroupId(String documentGroupId) {
        this.documentGroupId = documentGroupId;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public void setLatest(Boolean latest) {
        this.latest = latest;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }
}