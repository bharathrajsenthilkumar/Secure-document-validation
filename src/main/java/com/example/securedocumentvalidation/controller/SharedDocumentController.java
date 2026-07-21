package com.example.securedocumentvalidation.controller;

import com.example.securedocumentvalidation.dto.ShareRequestDTO;
import com.example.securedocumentvalidation.security.SharedDocumentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/share")
public class SharedDocumentController {

    private final SharedDocumentService service;

    public SharedDocumentController(
            SharedDocumentService service
    ) {
        this.service = service;
    }

    @PostMapping("/{documentId}")
    public ResponseEntity<String> shareDocument(
            @PathVariable Long documentId,
            @RequestBody ShareRequestDTO request,
            Authentication authentication
    ) {

        service.shareDocument(
                documentId,
                authentication.getName(),
                request.getUsername()
        );

        return ResponseEntity.ok(
                "Document shared successfully"
        );
    }
}