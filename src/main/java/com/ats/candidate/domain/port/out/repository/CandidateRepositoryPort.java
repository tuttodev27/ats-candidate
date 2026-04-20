package com.ats.candidate.domain.port.out.repository;

import com.ats.candidate.domain.model.Candidate;

public interface CandidateRepositoryPort {
    boolean existsByEmail(String email);
    Candidate save(Candidate candidate);

}
