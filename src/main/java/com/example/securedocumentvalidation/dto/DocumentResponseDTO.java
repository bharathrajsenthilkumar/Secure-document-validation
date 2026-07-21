package com.example.securedocumentvalidation.dto;

public class DocumentResponseDTO {

    private Long id;
    private String filename;
    private String ownerUsername;

    private Integer version;
    private Boolean latest;
    private String documentGroupId;

    public DocumentResponseDTO(
            Long id,
            String filename,
            String ownerUsername,
            Integer version,
            Boolean latest,
            String documentGroupId
    ) {
        this.id = id;
        this.filename = filename;
        this.ownerUsername = ownerUsername;
        this.version = version;
        this.latest = latest;
        this.documentGroupId = documentGroupId;
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

    public Integer getVersion() {
        return version;
    }

    public Boolean getLatest() {
        return latest;
    }

    public String getDocumentGroupId() {
        return documentGroupId;
    }
}
