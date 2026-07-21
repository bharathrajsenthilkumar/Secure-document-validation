package com.example.securedocumentvalidation.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "shared_documents")
public class SharedDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long documentId;

    private String ownerUsername;

    private String sharedWithUsername;

    public SharedDocument() {
    }

    public SharedDocument(
            Long documentId,
            String ownerUsername,
            String sharedWithUsername
    ) {
        this.documentId = documentId;
        this.ownerUsername = ownerUsername;
        this.sharedWithUsername = sharedWithUsername;
    }

    public Long getId() {
        return id;
    }

    public Long getDocumentId() {
        return documentId;
    }

    public String getOwnerUsername() {
        return ownerUsername;
    }

    public String getSharedWithUsername() {
        return sharedWithUsername;
    }
}
