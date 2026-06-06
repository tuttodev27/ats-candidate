package com.ats.candidate.domain.exception;

public class InvalidCatalogReferenceException extends RuntimeException {
    public InvalidCatalogReferenceException(String message) {
        super(message);
    }
}
