package com.example.securedocumentvalidation.controller;

import com.example.securedocumentvalidation.entity.Document;
import com.example.securedocumentvalidation.service.DocumentService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
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

    @GetMapping("/test")
    public String test() {
        return "Documents endpoint working";
    }

    // =========================
    // UPLOAD
    // =========================
    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> upload(
            @RequestParam("file") MultipartFile file,
            Authentication authentication) throws IOException {

        Document saved =
                service.uploadFile(file, authentication.getName());

        return ResponseEntity.ok(
                Map.of(
                        "message", "File uploaded successfully",
                        "documentId", saved.getId(),
                        "filename", saved.getFilename()
                )
        );
    }

    // =========================
    // DOWNLOAD
    // =========================
    @GetMapping("/{id}")
    public ResponseEntity<Resource> download(
            @PathVariable Long id,
            Authentication authentication) {

        boolean isAdmin = authentication.getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

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
                        "inline; filename=\"" + result.filename() + "\""
                )
                .body(new ByteArrayResource(result.data()));
    }

    // =========================
    // USER DOCUMENT LIST (PAGINATED)
    // =========================
    @GetMapping("/my")
    public ResponseEntity<?> getMyDocuments(
            Pageable pageable,
            Authentication authentication) {

        return ResponseEntity.ok(
                service.getUserDocuments(
                        authentication.getName(),
                        pageable
                )
        );
    }

    // =========================
    // ADMIN - ALL DOCUMENTS (PAGINATED)
    // =========================
    @GetMapping
    public ResponseEntity<?> getAllDocuments(
            Pageable pageable,
            Authentication authentication) {

        boolean isAdmin = authentication.getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin) {
            throw new AccessDeniedException("Forbidden");
        }

        return ResponseEntity.ok(
                service.getAllDocuments(pageable)
        );
    }
}
