package com.example.securedocumentvalidation.service;

import com.example.securedocumentvalidation.dto.DocumentResponseDTO;
import com.example.securedocumentvalidation.entity.AuditAction;
import com.example.securedocumentvalidation.entity.Document;
import com.example.securedocumentvalidation.repository.DocumentRepository;
import com.example.securedocumentvalidation.util.CryptoUtil;
import com.example.securedocumentvalidation.util.HashUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class DocumentService {

    private static final Logger log =
            LoggerFactory.getLogger(DocumentService.class);

    private static final String UPLOAD_DIR = "uploads";

    private final DocumentRepository repository;
    private final AuditService auditService;

    public DocumentService(DocumentRepository repository,
                           AuditService auditService) {
        this.repository = repository;
        this.auditService = auditService;
    }

    // =========================
    // LIST USER DOCUMENTS (DTO)
    // =========================
    public Page<DocumentResponseDTO> getUserDocuments(
            String username,
            Pageable pageable) {

        return repository.findByOwnerUsername(username, pageable)
                .map(doc -> new DocumentResponseDTO(
                        doc.getId(),
                        doc.getFilename(),
                        doc.getOwnerUsername()
                ));
    }

    // =========================
    // LIST ALL DOCUMENTS (ADMIN)
    // =========================
    public Page<DocumentResponseDTO> getAllDocuments(Pageable pageable) {

        return repository.findAll(pageable)
                .map(doc -> new DocumentResponseDTO(
                        doc.getId(),
                        doc.getFilename(),
                        doc.getOwnerUsername()
                ));
    }

    // =========================
    // UPLOAD DOCUMENT
    // =========================
    public Document uploadFile(
            MultipartFile file,
            String username) throws IOException {

        Files.createDirectories(Paths.get(UPLOAD_DIR));

        String storedFileName =
                System.currentTimeMillis() + "_" + file.getOriginalFilename();

        Path filePath = Paths.get(UPLOAD_DIR, storedFileName);

        byte[] originalBytes = file.getBytes();

        String hash = HashUtil.sha256(originalBytes);

        byte[] encryptedBytes = CryptoUtil.encrypt(originalBytes);

        Files.write(filePath, encryptedBytes);

        Document doc = new Document();
        doc.setFilename(file.getOriginalFilename());
        doc.setFilepath(filePath.toString());
        doc.setOwnerUsername(username);
        doc.setHash(hash);

        Document saved = repository.save(doc);

        // 🔥 AUDIT LOG
        auditService.log(username, AuditAction.UPLOAD, saved.getId());

        log.info("Document uploaded | id={} | owner={}",
                saved.getId(), username);

        return saved;
    }

    // =========================
    // DOWNLOAD DOCUMENT (AUTH + VERIFY)
    // =========================
    public DownloadedDocument downloadDocument(
            Long id,
            String username,
            boolean isAdmin) {

        Document doc = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        boolean isOwner = doc.getOwnerUsername().equals(username);

        if (!isOwner && !isAdmin) {

            // 🔥 AUDIT DENIED
            auditService.log(username, AuditAction.DENIED, id);

            throw new AccessDeniedException("Forbidden");
        }

        Path path = Paths.get(doc.getFilepath());

        if (!Files.exists(path)) {
            throw new RuntimeException("File missing from server");
        }

        try {
            byte[] encryptedBytes = Files.readAllBytes(path);

            byte[] decryptedBytes = CryptoUtil.decrypt(encryptedBytes);

            String currentHash = HashUtil.sha256(decryptedBytes);

            if (!currentHash.equals(doc.getHash())) {

                log.error("Integrity violation for document id={}", id);

                auditService.log(username, AuditAction.DENIED, id);

                throw new AccessDeniedException("File integrity compromised");
            }

            // 🔥 AUDIT SUCCESSFUL DOWNLOAD
            auditService.log(username, AuditAction.DOWNLOAD, id);

            return new DownloadedDocument(
                    doc.getFilename(),
                    decryptedBytes
            );

        } catch (IOException e) {
            throw new RuntimeException("Failed to read or verify file", e);
        }
    }

    // =========================
    // DOWNLOAD RESPONSE RECORD
    // =========================
    public record DownloadedDocument(String filename, byte[] data) {}
}
