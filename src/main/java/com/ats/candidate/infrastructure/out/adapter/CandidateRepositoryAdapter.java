package com.ats.candidate.infrastructure.out.adapter;

import com.ats.candidate.domain.model.Candidate;
import com.ats.candidate.domain.port.out.repository.CandidateRepositoryPort;
import com.ats.candidate.infrastructure.out.mapper.CandidatePersistenceMapper;
import com.ats.candidate.infrastructure.out.repository.CandidateJpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public class CandidateRepositoryAdapter implements CandidateRepositoryPort {

    private final CandidateJpaRepository candidateJpaRepository;
    private final CandidatePersistenceMapper candidatePersistenceMapper;

    public CandidateRepositoryAdapter(
            CandidateJpaRepository candidateJpaRepository,
            CandidatePersistenceMapper candidatePersistenceMapper
    ) {
        this.candidateJpaRepository = candidateJpaRepository;
        this.candidatePersistenceMapper = candidatePersistenceMapper;
    }

    @Override
    public boolean existsByEmail(String email) {
        if (email == null || email.isBlank()) {
            return false;
        }
        return candidateJpaRepository.existsByEmailIgnoreCase(email.trim());
    }

    @Override
    public Candidate save(Candidate candidate) {
        return candidatePersistenceMapper.toDomain(
                candidateJpaRepository.save(candidatePersistenceMapper.toEntity(candidate))
        );
    }

    @Override
    public java.util.Optional<Candidate> findById(Long id) {
        return candidateJpaRepository.findById(id)
                .map(candidatePersistenceMapper::toDomain);
    }

}
