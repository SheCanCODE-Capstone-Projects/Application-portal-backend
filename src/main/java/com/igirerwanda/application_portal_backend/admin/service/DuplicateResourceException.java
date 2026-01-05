package com.igirerwanda.application_portal_backend.admin.service;

/**
 * Thrown when attempting to create a resource that must be unique
 * and a record with the same unique key already exists.
 */
public class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException(String message) {
        super(message);
    }
}
