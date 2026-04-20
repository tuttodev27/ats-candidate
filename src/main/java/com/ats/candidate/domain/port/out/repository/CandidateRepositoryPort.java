package com.ats.candidate.domain.port.out.repository;

import com.ats.candidate.domain.model.Candidate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CandidateRepositoryPort {
    boolean existsByEmail(String email);
    Candidate save(Candidate candidate);
    Page<Candidate> findAll(Boolean active, Pageable pageable);

}
