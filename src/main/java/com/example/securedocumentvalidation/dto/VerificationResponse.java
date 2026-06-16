package com.example.securedocumentvalidation.dto;

public class VerificationResponse {

    private Long documentId;
    private String filename;
    private boolean verified;
    private String message;

    public VerificationResponse() {
    }

    public VerificationResponse(
            Long documentId,
            String filename,
            boolean verified,
            String message) {

        this.documentId = documentId;
        this.filename = filename;
        this.verified = verified;
        this.message = message;
    }

    public Long getDocumentId() {
        return documentId;
    }

    public void setDocumentId(Long documentId) {
        this.documentId = documentId;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}