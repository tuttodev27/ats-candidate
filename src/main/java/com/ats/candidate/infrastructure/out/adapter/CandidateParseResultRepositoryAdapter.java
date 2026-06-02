package com.ats.candidate.infrastructure.out.adapter;

import com.ats.candidate.domain.model.CandidateParseResult;
import com.ats.candidate.domain.port.out.repository.CandidateParseResultRepositoryPort;
import com.ats.candidate.infrastructure.out.mapper.CandidateParseResultPersistenceMapper;
import com.ats.candidate.infrastructure.out.repository.CandidateParseResultJpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public class CandidateParseResultRepositoryAdapter implements CandidateParseResultRepositoryPort {

    private final CandidateParseResultJpaRepository candidateParseResultJpaRepository;
    private final CandidateParseResultPersistenceMapper candidateParseResultPersistenceMapper;

    public CandidateParseResultRepositoryAdapter(
            CandidateParseResultJpaRepository candidateParseResultJpaRepository,
            CandidateParseResultPersistenceMapper candidateParseResultPersistenceMapper
    ) {
        this.candidateParseResultJpaRepository = candidateParseResultJpaRepository;
        this.candidateParseResultPersistenceMapper = candidateParseResultPersistenceMapper;
    }

    @Override
    public CandidateParseResult save(CandidateParseResult result) {
        return candidateParseResultPersistenceMapper.toDomain(
                candidateParseResultJpaRepository.save(candidateParseResultPersistenceMapper.toEntity(result))
        );
    }
}
