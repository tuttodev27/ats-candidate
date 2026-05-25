package com.ats.candidate.infrastructure.out.adapter;

import com.ats.candidate.domain.model.Candidate;
import com.ats.candidate.domain.port.out.repository.CandidateRepositoryPort;
import com.ats.candidate.infrastructure.out.mapper.CandidatePersistenceMapper;
import com.ats.candidate.infrastructure.out.repository.CandidateJpaRepository;
import com.ats.candidate.infrastructure.out.entity.CandidateEntity;
import org.springframework.data.domain.Page;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

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
    public boolean existsById(Long id) {
        return id != null && candidateJpaRepository.existsById(id);
    }

    @Override
    public Candidate save(Candidate candidate) {
        return candidatePersistenceMapper.toDomain(
                candidateJpaRepository.save(candidatePersistenceMapper.toEntity(candidate))
        );
    }

    @Override
    public Page<Candidate> findAll(Boolean active, Pageable pageable) {
        if (active == null) {
            return candidateJpaRepository.findAll(pageable)
                    .map(candidatePersistenceMapper::toDomain);
        }
        return candidateJpaRepository.findByActive(active, pageable)
                .map(candidatePersistenceMapper::toDomain);
    }

    @Override
    public Optional<Candidate> findById(Long id) {
        return candidateJpaRepository.findById(id)
                .map(candidatePersistenceMapper::toDomain);
    }

    @Override
    public Candidate update(Candidate candidate) {
        CandidateEntity existing = candidateJpaRepository.findById(candidate.getId())
                .orElseThrow(() -> new IllegalArgumentException("Candidate not found with id: " + candidate.getId()));

        existing.setPhone(candidate.getPhone());
        existing.setCountryCode(candidate.getCountryCode());
        existing.setIdentityDocument(candidate.getIdentityDocument());
        existing.setLocation(candidate.getLocation());
        existing.setLinkedinUrl(candidate.getLinkedinUrl());
        existing.setGithubUrl(candidate.getGithubUrl());
        existing.setBirthDate(candidate.getBirthDate());
        existing.setUpdatedAt(candidate.getUpdatedAt());
        existing.setUpdatedBy(candidate.getUpdatedBy());

        return candidatePersistenceMapper.toDomain(candidateJpaRepository.save(existing));
    }

}

