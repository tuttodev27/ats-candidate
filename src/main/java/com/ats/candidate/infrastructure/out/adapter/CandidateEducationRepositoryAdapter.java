package com.ats.candidate.infrastructure.out.adapter;

import com.ats.candidate.domain.model.CandidateEducation;
import com.ats.candidate.domain.port.out.repository.CandidateEducationRepositoryPort;
import com.ats.candidate.infrastructure.out.mapper.CandidateEducationPersistenceMapper;
import com.ats.candidate.infrastructure.out.repository.CandidateEducationJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CandidateEducationRepositoryAdapter implements CandidateEducationRepositoryPort {

    private final CandidateEducationJpaRepository jpaRepository;
    private final CandidateEducationPersistenceMapper mapper;

    public CandidateEducationRepositoryAdapter(
            CandidateEducationJpaRepository jpaRepository,
            CandidateEducationPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public List<CandidateEducation> findByCandidateId(Long candidateId) {
        return jpaRepository.findByCandidateId(candidateId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public CandidateEducation save(CandidateEducation education) {
        var entity = mapper.toEntity(education);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public void deleteByCandidateId(Long candidateId) {
        jpaRepository.deleteByCandidateId(candidateId);
    }
}
