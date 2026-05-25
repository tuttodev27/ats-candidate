package com.ats.candidate.infrastructure.out.adapter;

import com.ats.candidate.domain.model.CandidateExperience;
import com.ats.candidate.domain.port.out.repository.CandidateExperienceRepositoryPort;
import com.ats.candidate.infrastructure.out.mapper.CandidateExperiencePersistenceMapper;
import com.ats.candidate.infrastructure.out.repository.CandidateExperienceJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
public class CandidateExperienceRepositoryAdapter implements CandidateExperienceRepositoryPort {

    private final CandidateExperienceJpaRepository experienceJpaRepository;
    private final CandidateExperiencePersistenceMapper experiencePersistenceMapper;

    public CandidateExperienceRepositoryAdapter(
            CandidateExperienceJpaRepository experienceJpaRepository,
            CandidateExperiencePersistenceMapper experiencePersistenceMapper
    ) {
        this.experienceJpaRepository = experienceJpaRepository;
        this.experiencePersistenceMapper = experiencePersistenceMapper;
    }

    @Override
    public Set<CandidateExperience> saveAll(Set<CandidateExperience> experiences) {
        if (experiences == null || experiences.isEmpty()) {
            return Set.of();
        }
        return new HashSet<>(experienceJpaRepository.saveAll(experiences.stream()
                .map(experiencePersistenceMapper::toEntity)
                .toList())
                .stream()
                .map(experiencePersistenceMapper::toDomain)
                .toList());
    }

    @Override
    public List<CandidateExperience> findByCandidateId(Long candidateId) {
        return experienceJpaRepository.findByCandidateId(candidateId)
                .stream()
                .map(experiencePersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public void deleteAllByCandidateId(Long candidateId) {
        experienceJpaRepository.deleteAllByCandidateId(candidateId);
    }
}
