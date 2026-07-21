package com.example.securedocumentvalidation.service;

import com.example.securedocumentvalidation.dto.DocumentResponseDTO;
import com.example.securedocumentvalidation.dto.VerificationResponse;
import com.example.securedocumentvalidation.entity.AuditAction;
import com.example.securedocumentvalidation.entity.Document;
import com.example.securedocumentvalidation.entity.SharedDocument;
import com.example.securedocumentvalidation.repository.DocumentRepository;
import com.example.securedocumentvalidation.repository.SharedDocumentRepository;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class DocumentService {

    private static final Logger log =
            LoggerFactory.getLogger(DocumentService.class);

    private static final String UPLOAD_DIR = "uploads";

    private final DocumentRepository repository;
    private final AuditService auditService;
    private final SharedDocumentRepository sharedDocumentRepository;

    public DocumentService(
            DocumentRepository repository,
            AuditService auditService,
            SharedDocumentRepository sharedDocumentRepository
    ) {
        this.repository = repository;
        this.auditService = auditService;
        this.sharedDocumentRepository = sharedDocumentRepository;
    }
    // =========================
// LIST USER DOCUMENTS (OWN + SHARED)
// =========================
    public Page<DocumentResponseDTO> getUserDocuments(
            String username,
            Pageable pageable) {

        return repository
                .findUserAndSharedDocuments(
                        username,
                        pageable
                )
                .map(doc ->
                        new DocumentResponseDTO(
                                doc.getId(),
                                doc.getFilename(),
                                doc.getOwnerUsername(),
                                doc.getVersion(),
                                doc.getLatest(),
                                doc.getDocumentGroupId()
                        ));
    }

    // =========================
// LIST ALL DOCUMENTS (ADMIN)
// =========================
    public Page<DocumentResponseDTO> getAllDocuments(
            Pageable pageable) {

        return repository
                .findAll(pageable)
                .map(doc ->
                        new DocumentResponseDTO(
                                doc.getId(),
                                doc.getFilename(),
                                doc.getOwnerUsername(),
                                doc.getVersion(),
                                doc.getLatest(),
                                doc.getDocumentGroupId()
                        ));
    }
    // =========================
    // SEARCH BY FILENAME
    // =========================
    public Page<DocumentResponseDTO> searchByFilename(
            String filename,
            String username,
            boolean isAdmin,
            Pageable pageable) {

        Page<Document> documents;

        if (isAdmin) {

            documents = repository
                    .findByFilenameContainingIgnoreCase(
                            filename,
                            pageable
                    );

        } else {

            documents = repository
                    .findByFilenameContainingIgnoreCaseAndOwnerUsername(
                            filename,
                            username,
                            pageable
                    );
        }

        return documents.map(doc ->
                new DocumentResponseDTO(
                        doc.getId(),
                        doc.getFilename(),
                        doc.getOwnerUsername(),
                        doc.getVersion(),
                        doc.getLatest(),
                        doc.getDocumentGroupId()
                ));
    }

    // =========================
// SEARCH BY OWNER
// =========================
    public Page<DocumentResponseDTO> searchByOwner(
            String owner,
            String username,
            boolean isAdmin,
            Pageable pageable) {

        if (isAdmin) {

            return repository
                    .findByOwnerUsernameContainingIgnoreCase(
                            owner,
                            pageable
                    )
                    .map(doc ->
                            new DocumentResponseDTO(
                                    doc.getId(),
                                    doc.getFilename(),
                                    doc.getOwnerUsername(),
                                    doc.getVersion(),
                                    doc.getLatest(),
                                    doc.getDocumentGroupId()
                            ));
        }

        if (!owner.equalsIgnoreCase(username)) {

            throw new AccessDeniedException(
                    "You are not authorized to access other users documents"
            );
        }

        return repository
                .findByOwnerUsername(
                        username,
                        pageable
                )
                .map(doc ->
                        new DocumentResponseDTO(
                                doc.getId(),
                                doc.getFilename(),
                                doc.getOwnerUsername(),
                                doc.getVersion(),
                                doc.getLatest(),
                                doc.getDocumentGroupId()
                        ));
    }
    // =========================
// UPLOAD DOCUMENT
// =========================
    public Document uploadFile(
            MultipartFile file,
            String username
    ) throws IOException {

        Files.createDirectories(Paths.get(UPLOAD_DIR));

        String storedFileName =
                System.currentTimeMillis()
                        + "_"
                        + file.getOriginalFilename();

        Path filePath =
                Paths.get(UPLOAD_DIR, storedFileName);

        byte[] originalBytes = file.getBytes();

        String hash =
                HashUtil.sha256(originalBytes);

        byte[] encryptedBytes =
                CryptoUtil.encrypt(originalBytes);

        Files.write(filePath, encryptedBytes);

        Document doc = new Document();

        doc.setFilename(file.getOriginalFilename());
        doc.setFilepath(filePath.toString());
        doc.setOwnerUsername(username);
        doc.setHash(hash);

        // Version History
        List<Document> existing =
                repository.findByOwnerUsername(username);

        Document latest = existing.stream()
                .filter(d ->
                        d.getFilename().equals(file.getOriginalFilename()) &&
                                Boolean.TRUE.equals(d.getLatest()))
                .findFirst()
                .orElse(null);

        if (latest == null) {

            doc.setDocumentGroupId(UUID.randomUUID().toString());
            doc.setVersion(1);
            doc.setLatest(true);

        } else {

            latest.setLatest(false);
            repository.save(latest);

            doc.setDocumentGroupId(latest.getDocumentGroupId());
            doc.setVersion(latest.getVersion() + 1);
            doc.setLatest(true);
        }

        Document saved = repository.save(doc);

        auditService.log(
                username,
                AuditAction.UPLOAD,
                saved.getId()
        );

        log.info(
                "Document uploaded | id={} | owner={}",
                saved.getId(),
                username
        );

        return saved;
    }// =========================
    // VERSION HISTORY
// =========================
    public List<DocumentResponseDTO> getVersionHistory(
            String documentGroupId
    ) {

        return repository
                .findByDocumentGroupIdOrderByVersionDesc(
                        documentGroupId
                )
                .stream()
                .map(doc ->
                        new DocumentResponseDTO(
                                doc.getId(),
                                doc.getFilename(),
                                doc.getOwnerUsername(),
                                doc.getVersion(),
                                doc.getLatest(),
                                doc.getDocumentGroupId()
                        )
                )
                .toList();
    }
    // =========================
    // DOWNLOAD DOCUMENT
    // =========================
    public DownloadedDocument downloadDocument(
            Long id,
            String username,
            boolean isAdmin) {

        Document doc = repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Document not found")
                );

        boolean isOwner =
                doc.getOwnerUsername().equals(username);

        boolean isShared =
                sharedDocumentRepository
                        .findByDocumentIdAndSharedWithUsername(
                                id,
                                username
                        )
                        .isPresent();

// ACCESS CHECK
        if (!isOwner &&
                !isAdmin &&
                !isShared) {

            auditService.log(
                    username,
                    AuditAction.DENIED,
                    id
            );

            throw new AccessDeniedException(
                    "Forbidden"
            );
        }

        Path path = Paths.get(doc.getFilepath());

        if (!Files.exists(path)) {
            throw new RuntimeException(
                    "File missing from server"
            );
        }

        try {

            // READ ENCRYPTED FILE
            byte[] encryptedBytes =
                    Files.readAllBytes(path);

            // DECRYPT FILE
            byte[] decryptedBytes =
                    CryptoUtil.decrypt(encryptedBytes);

            // VERIFY HASH
            String currentHash =
                    HashUtil.sha256(decryptedBytes);

            if (!currentHash.equals(doc.getHash())) {

                log.error(
                        "Integrity violation for document id={}",
                        id
                );

                auditService.log(
                        username,
                        AuditAction.DENIED,
                        id
                );

                throw new AccessDeniedException(
                        "File integrity compromised"
                );
            }

            // AUDIT SUCCESSFUL DOWNLOAD
            auditService.log(
                    username,
                    AuditAction.DOWNLOAD,
                    id
            );

            return new DownloadedDocument(
                    doc.getFilename(),
                    decryptedBytes
            );

        } catch (Exception e) {

            log.error(
                    "Integrity violation for document id={}",
                    id
            );

            // AUDIT FAILED ACCESS
            auditService.log(
                    username,
                    AuditAction.DENIED,
                    id
            );

            throw new AccessDeniedException(
                    "File integrity compromised"
            );
        }
    }
    // =========================
// VERIFY DOCUMENT
// =========================
    public VerificationResponse verifyDocument(Long id) {

        Document doc = repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Document not found"
                        )
                );

        try {

            Path path =
                    Paths.get(doc.getFilepath());

            if (!Files.exists(path)) {

                return new VerificationResponse(
                        doc.getId(),
                        doc.getFilename(),
                        false,
                        "File not found on server"
                );
            }

            byte[] encryptedBytes =
                    Files.readAllBytes(path);

            byte[] decryptedBytes =
                    CryptoUtil.decrypt(
                            encryptedBytes
                    );

            String currentHash =
                    HashUtil.sha256(
                            decryptedBytes
                    );

            boolean verified =
                    currentHash.equals(
                            doc.getHash()
                    );

            if (verified) {

                auditService.log(
                        doc.getOwnerUsername(),
                        AuditAction.VERIFY,
                        id
                );
            }

            return new VerificationResponse(
                    doc.getId(),
                    doc.getFilename(),
                    verified,
                    verified
                            ? "Document integrity verified successfully"
                            : "Document integrity compromised"
            );

        } catch (Exception e) {

            return new VerificationResponse(
                    doc.getId(),
                    doc.getFilename(),
                    false,
                    "Verification failed"
            );
        }
    }
    // =========================
// DELETE DOCUMENT
// =========================
    public void deleteDocument(
            Long id,
            String username,
            boolean isAdmin) {

        Document doc = repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Document not found"));

        boolean isOwner =
                doc.getOwnerUsername().equals(username);

        // ACCESS CHECK
        if (!isOwner && !isAdmin) {

            auditService.log(
                    username,
                    AuditAction.DENIED,
                    id
            );

            throw new AccessDeniedException("Forbidden");
        }

        try {

            Path path = Paths.get(doc.getFilepath());

            if (Files.exists(path)) {
                Files.delete(path);
            }

            repository.delete(doc);

            auditService.log(
                    username,
                    AuditAction.DELETE,
                    id
            );

            log.info(
                    "Document deleted | id={} | user={}",
                    id,
                    username
            );

        } catch (Exception e) {

            e.printStackTrace();

            throw new RuntimeException(
                    "Failed to delete document: "
                            + e.getMessage()
            );
        }
    }
    // =========================
    // DOWNLOAD RESPONSE RECORD
    // =========================
    public record DownloadedDocument(
            String filename,
            byte[] data
    ) {}
    // =========================
// GET SHARED DOCUMENTS
// =========================
    public List<DocumentResponseDTO> getSharedDocuments(
            String username
    ) {

        List<DocumentResponseDTO> response = new ArrayList<>();

        List<com.example.securedocumentvalidation.entity.SharedDocument> sharedDocs =
                sharedDocumentRepository.findBySharedWithUsername(username);

        for (var shared : sharedDocs) {

            repository.findById(shared.getDocumentId())
                    .ifPresent(doc ->
                            response.add(
                                    new DocumentResponseDTO(
                                            doc.getId(),
                                            doc.getFilename(),
                                            doc.getOwnerUsername(),
                                            doc.getVersion(),
                                            doc.getLatest(),
                                            doc.getDocumentGroupId()
                                    )
                            ));
        }

        return response;
    }
}