package com.ats.candidate.infrastructure.out.adapter;

import com.ats.candidate.domain.model.CandidateState;
import com.ats.candidate.domain.port.out.repository.CandidateStateRepositoryPort;
import com.ats.candidate.infrastructure.out.mapper.CandidateStatePersistenceMapper;
import com.ats.candidate.infrastructure.out.repository.CandidateStateJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CandidateStateRepositoryAdapter implements CandidateStateRepositoryPort {

    private final CandidateStateJpaRepository jpaRepository;
    private final CandidateStatePersistenceMapper mapper;

    public CandidateStateRepositoryAdapter(
            CandidateStateJpaRepository jpaRepository,
            CandidateStatePersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public CandidateState save(CandidateState state) {
        var entity = mapper.toEntity(state);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public List<CandidateState> findByCandidateIdOrderByCreatedAtDesc(Long candidateId) {
        return jpaRepository.findByCandidateIdOrderByCreatedAtDesc(candidateId).stream()
                .map(mapper::toDomain)
                .toList();
    }
}
