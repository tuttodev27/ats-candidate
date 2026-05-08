package com.ats.candidate.domain.exception;

public class CandidateNotFoundException extends RuntimeException {
    public CandidateNotFoundException(Long id)
    {
        super("Candidate with id " + id + " not found");
    }
}
