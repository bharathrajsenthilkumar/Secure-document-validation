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

    // 🔐 integrity hash
    @Column(nullable = false, length = 64)
    private String hash;

    // ===== getters =====
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

    public String getHash() {
        return hash;
    }

    // ===== setters =====
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

    public void setHash(String hash) {
        this.hash = hash;
    }
}
