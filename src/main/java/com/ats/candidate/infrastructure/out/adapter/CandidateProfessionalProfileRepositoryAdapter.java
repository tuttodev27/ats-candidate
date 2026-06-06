package com.ats.candidate.infrastructure.out.adapter;

import com.ats.candidate.domain.model.CandidateProfessionalProfile;
import com.ats.candidate.domain.port.out.repository.CandidateProfessionalProfileRepositoryPort;
import com.ats.candidate.infrastructure.out.mapper.CandidateProfessionalProfilePersistenceMapper;
import com.ats.candidate.infrastructure.out.repository.CandidateProfessionalProfileJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class CandidateProfessionalProfileRepositoryAdapter implements CandidateProfessionalProfileRepositoryPort {

    private final CandidateProfessionalProfileJpaRepository professionalProfileJpaRepository;
    private final CandidateProfessionalProfilePersistenceMapper professionalProfilePersistenceMapper;

    public CandidateProfessionalProfileRepositoryAdapter(
            CandidateProfessionalProfileJpaRepository professionalProfileJpaRepository,
            CandidateProfessionalProfilePersistenceMapper professionalProfilePersistenceMapper
    ) {
        this.professionalProfileJpaRepository = professionalProfileJpaRepository;
        this.professionalProfilePersistenceMapper = professionalProfilePersistenceMapper;
    }

    @Override
    public CandidateProfessionalProfile save(CandidateProfessionalProfile professionalProfile) {
        return professionalProfilePersistenceMapper.toDomain(
                professionalProfileJpaRepository.save(professionalProfilePersistenceMapper.toEntity(professionalProfile))
        );
    }

    @Override
    public Optional<CandidateProfessionalProfile> findByCandidateId(Long candidateId) {
        return professionalProfileJpaRepository.findByCandidateId(candidateId)
                .map(professionalProfilePersistenceMapper::toDomain);
    }
}
