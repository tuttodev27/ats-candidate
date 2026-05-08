package com.ats.candidate.domain.port.out.repository;

import com.ats.candidate.domain.model.CandidateState;

public interface CandidateStateRepositoryPort {
    CandidateState save(CandidateState candidateState);
}
