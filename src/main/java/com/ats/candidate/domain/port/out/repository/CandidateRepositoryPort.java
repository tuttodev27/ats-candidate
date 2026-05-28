package com.ats.candidate.domain.port.out.repository;

import com.ats.candidate.domain.model.Candidate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CandidateRepositoryPort {
    boolean existsByEmail(String email);
    boolean existsById(Long id);
    Candidate save(Candidate candidate);
    Candidate update(Candidate candidate);
    Page<Candidate> findAll(Boolean active, String search, Pageable pageable);
    Optional<Candidate> findById(Long id);
    void deactivate(Long id, Long recruiterId);
}

