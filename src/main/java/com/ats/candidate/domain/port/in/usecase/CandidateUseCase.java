package com.ats.candidate.domain.port.in.usecase;

import com.ats.candidate.domain.model.Candidate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CandidateUseCase {
    Candidate create(Candidate candidate, Long recruiterId);
    Candidate update(Long id, Candidate candidate, Long recruiterId);
    Page<Candidate> list(Boolean active, String search, Pageable pageable);
    Candidate getById(Long id);
    Candidate updateStatus(Long id, String status, Long recruiterId);
    void deactivate(Long id, Long recruiterId);
}

