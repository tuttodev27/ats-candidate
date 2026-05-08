package com.ats.candidate.infrastructure.out.adapter;

import com.ats.candidate.domain.model.CandidateState;
import com.ats.candidate.domain.port.out.repository.CandidateStateRepositoryPort;
import com.ats.candidate.infrastructure.out.mapper.CandidateStatePersistenceMapper;
import com.ats.candidate.infrastructure.out.repository.CandidateStateJpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public class CandidateStateRepositoryAdapter implements CandidateStateRepositoryPort {

    private final CandidateStateJpaRepository candidateStateJpaRepository;
    private final CandidateStatePersistenceMapper candidateStatePersistenceMapper;

    public CandidateStateRepositoryAdapter(
            CandidateStateJpaRepository candidateStateJpaRepository,
            CandidateStatePersistenceMapper candidateStatePersistenceMapper
    ) {
        this.candidateStateJpaRepository = candidateStateJpaRepository;
        this.candidateStatePersistenceMapper = candidateStatePersistenceMapper;
    }

    @Override
    public CandidateState save(CandidateState candidateState) {
        return candidateStatePersistenceMapper.toDomain(
                candidateStateJpaRepository.save(candidateStatePersistenceMapper.toEntity(candidateState))
        );
    }
}
