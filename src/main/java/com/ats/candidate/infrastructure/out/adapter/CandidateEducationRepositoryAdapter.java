package com.ats.candidate.infrastructure.out.adapter;

import com.ats.candidate.domain.model.CandidateEducation;
import com.ats.candidate.domain.port.out.repository.CandidateEducationRepositoryPort;
import com.ats.candidate.infrastructure.out.mapper.CandidateEducationPersistenceMapper;
import com.ats.candidate.infrastructure.out.repository.CandidateEducationJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
public class CandidateEducationRepositoryAdapter implements CandidateEducationRepositoryPort {

    private final CandidateEducationJpaRepository educationJpaRepository;
    private final CandidateEducationPersistenceMapper educationPersistenceMapper;

    public CandidateEducationRepositoryAdapter(
            CandidateEducationJpaRepository educationJpaRepository,
            CandidateEducationPersistenceMapper educationPersistenceMapper
    ) {
        this.educationJpaRepository = educationJpaRepository;
        this.educationPersistenceMapper = educationPersistenceMapper;
    }

    @Override
    public Set<CandidateEducation> saveAll(Set<CandidateEducation> educations) {
        return new HashSet<>(educationJpaRepository.saveAll(educations.stream()
                .map(educationPersistenceMapper::toEntity)
                .toList())
                .stream()
                .map(educationPersistenceMapper::toDomain)
                .toList());
    }

    @Override
    public List<CandidateEducation> findByCandidateId(Long candidateId) {
        return educationJpaRepository.findByCandidateId(candidateId)
                .stream()
                .map(educationPersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public void deleteAllByCandidateId(Long candidateId) {
        educationJpaRepository.deleteAllByCandidateId(candidateId);
    }
}

