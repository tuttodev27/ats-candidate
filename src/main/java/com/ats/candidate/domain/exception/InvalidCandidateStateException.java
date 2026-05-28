package com.ats.candidate.domain.exception;

public class InvalidCandidateStateException extends RuntimeException {
    public InvalidCandidateStateException(String message) {
        super(message);
    }
}
