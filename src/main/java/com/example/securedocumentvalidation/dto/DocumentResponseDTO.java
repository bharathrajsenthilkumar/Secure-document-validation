package com.example.securedocumentvalidation.dto;

public class DocumentResponseDTO {

    private Long id;
    private String filename;
    private String ownerUsername;

    public DocumentResponseDTO(Long id, String filename, String ownerUsername) {
        this.id = id;
        this.filename = filename;
        this.ownerUsername = ownerUsername;
    }

    public Long getId() {
        return id;
    }

    public String getFilename() {
        return filename;
    }

    public String getOwnerUsername() {
        return ownerUsername;
    }
}
