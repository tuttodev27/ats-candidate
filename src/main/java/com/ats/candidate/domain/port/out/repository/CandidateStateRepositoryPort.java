package com.ats.candidate.domain.port.out.repository;

import com.ats.candidate.domain.model.CandidateState;

import java.util.List;
import java.util.Optional;

public interface CandidateStateRepositoryPort {
    CandidateState save(CandidateState candidateState);
    Optional<CandidateState> findLatestByCandidateId(Long candidateId);
    List<CandidateState> findAllByCandidateId(Long candidateId);
}
