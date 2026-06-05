package com.ats.candidate.domain.exception;

public class InvalidCatalogReferenceException extends RuntimeException {
    public InvalidCatalogReferenceException(String catalog, Long id) {
        super("Invalid " + catalog + " reference: " + id);
    }
}
