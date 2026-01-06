package com.igirerwanda.application_portal_backend.common.exception;

import com.igirerwanda.application_portal_backend.admin.service.DuplicateResourceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatusCode;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Object> handleNotFound(NotFoundException ex, WebRequest request) {
        log.warn("Resource not found: {}", ex.getMessage());
        return build(HttpStatus.NOT_FOUND, "Resource not found", ex.getMessage(), request.getDescription(false));
    }

    @ExceptionHandler({ValidationException.class, DuplicateResourceException.class, IllegalArgumentException.class})
    public ResponseEntity<Object> handleBadRequest(RuntimeException ex, WebRequest request) {
        log.warn("Bad request: {}", ex.getMessage());
        return build(HttpStatus.BAD_REQUEST, "Invalid request", ex.getMessage(), request.getDescription(false));
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<Object> handleSecurityException(SecurityException ex, WebRequest request) {
        log.warn("Security violation: {}", ex.getMessage());
        return build(HttpStatus.FORBIDDEN, "Access denied", ex.getMessage(), request.getDescription(false));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDenied(AccessDeniedException ex, WebRequest request) {
        log.warn("Access denied: {}", ex.getMessage());
        return build(HttpStatus.FORBIDDEN, "Access denied", "You don't have permission to access this resource", request.getDescription(false));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleDataIntegrityViolation(DataIntegrityViolationException ex, WebRequest request) {
        log.error("Data integrity violation: {}", ex.getMessage());
        return build(HttpStatus.CONFLICT, "Data conflict", "The operation conflicts with existing data", request.getDescription(false));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleUnexpected(Exception ex, WebRequest request) {
        log.error("Unexpected error occurred", ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", 
                "An unexpected error occurred. Please try again later.", request.getDescription(false));
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        log.warn("Validation failed for request: {}", request.getDescription(false));
        
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(err -> {
            String field = (err instanceof FieldError fe) ? fe.getField() : err.getObjectName();
            String message = err.getDefaultMessage();
            fieldErrors.put(field, message);
        });
        
        return buildWithFieldErrors(HttpStatus.BAD_REQUEST, "Validation failed", 
                "Please check the provided data and try again", fieldErrors, request.getDescription(false));
    }

    private ResponseEntity<Object> build(HttpStatus status, String error, String message, String path) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", status.value());
        body.put("error", error);
        body.put("message", message);
        body.put("path", path);
        body.put("success", false);
        return new ResponseEntity<>(body, status);
    }

    private ResponseEntity<Object> buildWithFieldErrors(HttpStatus status, String error, String message, 
                                                        Map<String, String> fieldErrors, String path) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", status.value());
        body.put("error", error);
        body.put("message", message);
        body.put("path", path);
        body.put("success", false);
        body.put("fieldErrors", fieldErrors);
        return new ResponseEntity<>(body, status);
    }
}
