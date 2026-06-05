package com.ats.candidate.domain.port.out.repository;

import com.ats.candidate.domain.model.Candidate;

import java.util.Optional;

public interface CandidateRepositoryPort {
    boolean existsByEmail(String email);
    Candidate save(Candidate candidate);
    Optional<Candidate> findById(Long id);

}
