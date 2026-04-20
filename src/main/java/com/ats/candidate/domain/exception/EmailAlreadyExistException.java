package com.ats.candidate.domain.exception;

public class EmailAlreadyExistException extends RuntimeException {
    public EmailAlreadyExistException(String email) {
        super("Candidate already exists with email: " + email);
    }

}
