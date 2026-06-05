package com.ats.candidate.infrastructure.out.adapter;

import com.ats.candidate.domain.model.CandidateProfessionalProfile;
import com.ats.candidate.domain.port.out.repository.CandidateProfessionalProfileRepositoryPort;
import com.ats.candidate.infrastructure.out.mapper.CandidateProfessionalProfilePersistenceMapper;
import com.ats.candidate.infrastructure.out.repository.CandidateProfessionalProfileJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class CandidateProfessionalProfileRepositoryAdapter implements CandidateProfessionalProfileRepositoryPort {

    private final CandidateProfessionalProfileJpaRepository jpaRepository;
    private final CandidateProfessionalProfilePersistenceMapper mapper;

    public CandidateProfessionalProfileRepositoryAdapter(
            CandidateProfessionalProfileJpaRepository jpaRepository,
            CandidateProfessionalProfilePersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public CandidateProfessionalProfile save(CandidateProfessionalProfile profile) {
        var entity = mapper.toEntity(profile);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<CandidateProfessionalProfile> findByCandidateId(Long candidateId) {
        return jpaRepository.findByCandidateId(candidateId)
                .map(mapper::toDomain);
    }

    @Override
    public void deleteByCandidateId(Long candidateId) {
        jpaRepository.deleteByCandidateId(candidateId);
    }
}
