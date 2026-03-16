package com.example.securedocumentvalidation.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ===============================
    // Validation Errors (@Valid)
    // ===============================
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationErrors(MethodArgumentNotValidException ex) {

        Map<String, Object> response = buildBaseResponse(HttpStatus.BAD_REQUEST);

        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error ->
                        fieldErrors.put(error.getField(), error.getDefaultMessage())
                );

        response.put("errors", fieldErrors);

        return ResponseEntity.badRequest().body(response);
    }

    // ===============================
    // Access Denied (403)
    // ===============================
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDenied(AccessDeniedException ex) {

        Map<String, Object> response = buildBaseResponse(HttpStatus.FORBIDDEN);
        response.put("message", "You are not authorized to access this resource");

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    // ===============================
    // Generic Runtime Errors
    // ===============================
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntimeException(RuntimeException ex) {

        Map<String, Object> response = buildBaseResponse(HttpStatus.BAD_REQUEST);
        response.put("message", ex.getMessage());

        return ResponseEntity.badRequest().body(response);
    }

    private Map<String, Object> buildBaseResponse(HttpStatus status) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", status.value());
        response.put("error", status.getReasonPhrase());
        return response;
    }
}
