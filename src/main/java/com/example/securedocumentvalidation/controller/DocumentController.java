package com.example.securedocumentvalidation.controller;

import com.example.securedocumentvalidation.dto.VerificationResponse;
import com.example.securedocumentvalidation.entity.Document;
import com.example.securedocumentvalidation.service.DocumentService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final DocumentService service;

    public DocumentController(DocumentService service) {
        this.service = service;
    }

    // =========================
    // TEST
    // =========================
    @GetMapping("/test")
    public String test() {
        return "Documents endpoint working";
    }

    // =========================
    // UPLOAD DOCUMENT
    // =========================
    @PostMapping(
            value = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<Map<String, Object>> upload(
            @RequestParam("file") MultipartFile file,
            Authentication authentication
    ) throws IOException {

        Document saved =
                service.uploadFile(
                        file,
                        authentication.getName()
                );

        return ResponseEntity.ok(
                Map.of(
                        "message", "File uploaded successfully",
                        "documentId", saved.getId(),
                        "filename", saved.getFilename()
                )
        );
    }

    // =========================
    // DOWNLOAD DOCUMENT
    // =========================
    @GetMapping("/{id}")
    public ResponseEntity<Resource> download(
            @PathVariable Long id,
            Authentication authentication
    ) {

        boolean isAdmin = authentication.getAuthorities()
                .stream()
                .anyMatch(a ->
                        a.getAuthority().equals("ROLE_ADMIN"));

        DocumentService.DownloadedDocument result =
                service.downloadDocument(
                        id,
                        authentication.getName(),
                        isAdmin
                );

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" +
                                result.filename() + "\""
                )
                .body(new ByteArrayResource(result.data()));
    }

    // =========================
    // VERIFY DOCUMENT
    // =========================
    @GetMapping("/verify/{id}")
    public ResponseEntity<VerificationResponse> verifyDocument(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                service.verifyDocument(id)
        );
    }

    // =========================
    // DELETE DOCUMENT
    // =========================
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDocument(
            @PathVariable Long id,
            Authentication authentication
    ) {

        boolean isAdmin = authentication.getAuthorities()
                .stream()
                .anyMatch(a ->
                        a.getAuthority()
                                .equals("ROLE_ADMIN"));

        service.deleteDocument(
                id,
                authentication.getName(),
                isAdmin
        );

        return ResponseEntity.ok(
                Map.of(
                        "message",
                        "Document deleted successfully"
                )
        );
    }

    // =========================
    // MY DOCUMENTS
    // =========================
    @GetMapping("/my")
    public ResponseEntity<?> getMyDocuments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication
    ) {

        Pageable pageable =
                PageRequest.of(page, size);

        return ResponseEntity.ok(
                service.getUserDocuments(
                        authentication.getName(),
                        pageable
                )
        );
    }
    // =========================
// SHARED DOCUMENTS
// =========================
    @GetMapping("/shared")
    public ResponseEntity<?> getSharedDocuments(
            Authentication authentication
    ) {

        return ResponseEntity.ok(
                service.getSharedDocuments(
                        authentication.getName()
                )
        );
    }

    // =========================
    // ADMIN - ALL DOCUMENTS
    // =========================
    @GetMapping
    public ResponseEntity<?> getAllDocuments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication
    ) {

        boolean isAdmin = authentication.getAuthorities()
                .stream()
                .anyMatch(a ->
                        a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin) {
            throw new AccessDeniedException("Forbidden");
        }

        Pageable pageable =
                PageRequest.of(page, size);

        return ResponseEntity.ok(
                service.getAllDocuments(pageable)
        );
    }
    // =========================
// DOCUMENT VERSION HISTORY
// =========================
    @GetMapping("/versions/{documentGroupId}")
    public ResponseEntity<?> getVersionHistory(
            @PathVariable String documentGroupId
    ) {
        return ResponseEntity.ok(
                service.getVersionHistory(documentGroupId)
        );
    }

    // =========================
    // SEARCH BY FILENAME
    // =========================
    @GetMapping("/search/filename")
    public ResponseEntity<?> searchByFilename(
            @RequestParam String filename,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication
    ) {

        boolean isAdmin = authentication.getAuthorities()
                .stream()
                .anyMatch(a ->
                        a.getAuthority().equals("ROLE_ADMIN"));

        Pageable pageable =
                PageRequest.of(page, size);

        return ResponseEntity.ok(
                service.searchByFilename(
                        filename,
                        authentication.getName(),
                        isAdmin,
                        pageable
                )
        );
    }

    // =========================
    // SEARCH BY OWNER
    // =========================
    @GetMapping("/search/owner")
    public ResponseEntity<?> searchByOwner(
            @RequestParam String owner,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication
    ) {

        boolean isAdmin = authentication.getAuthorities()
                .stream()
                .anyMatch(a ->
                        a.getAuthority().equals("ROLE_ADMIN"));

        Pageable pageable =
                PageRequest.of(page, size);

        return ResponseEntity.ok(
                service.searchByOwner(
                        owner,
                        authentication.getName(),
                        isAdmin,
                        pageable
                )
        );
    }
}