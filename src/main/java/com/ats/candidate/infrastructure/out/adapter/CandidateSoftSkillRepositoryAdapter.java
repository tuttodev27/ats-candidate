package com.ats.candidate.infrastructure.out.adapter;

import com.ats.candidate.domain.model.CandidateSoftSkill;
import com.ats.candidate.domain.port.out.repository.CandidateSoftSkillRepositoryPort;
import com.ats.candidate.infrastructure.out.mapper.CandidateSoftSkillPersistenceMapper;
import com.ats.candidate.infrastructure.out.repository.CandidateSoftSkillJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CandidateSoftSkillRepositoryAdapter implements CandidateSoftSkillRepositoryPort {

    private final CandidateSoftSkillJpaRepository jpaRepository;
    private final CandidateSoftSkillPersistenceMapper mapper;

    public CandidateSoftSkillRepositoryAdapter(
            CandidateSoftSkillJpaRepository jpaRepository,
            CandidateSoftSkillPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public List<CandidateSoftSkill> findByCandidateId(Long candidateId) {
        return jpaRepository.findByCandidateId(candidateId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public CandidateSoftSkill save(CandidateSoftSkill softSkill) {
        var entity = mapper.toEntity(softSkill);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public void deleteByCandidateId(Long candidateId) {
        jpaRepository.deleteByCandidateId(candidateId);
    }
}
