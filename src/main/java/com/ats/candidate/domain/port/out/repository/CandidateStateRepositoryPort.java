package com.ats.candidate.domain.port.out.repository;

import com.ats.candidate.domain.model.CandidateState;

import java.util.List;

public interface CandidateStateRepositoryPort {
    CandidateState save(CandidateState state);
    List<CandidateState> findByCandidateIdOrderByCreatedAtDesc(Long candidateId);
}
