package com.ats.candidate.infrastructure.out.adapter;

import com.ats.candidate.domain.model.CandidateHardSkill;
import com.ats.candidate.domain.port.out.repository.CandidateHardSkillRepositoryPort;
import com.ats.candidate.infrastructure.out.mapper.CandidateHardSkillPersistenceMapper;
import com.ats.candidate.infrastructure.out.repository.CandidateHardSkillJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CandidateHardSkillRepositoryAdapter implements CandidateHardSkillRepositoryPort {

    private final CandidateHardSkillJpaRepository jpaRepository;
    private final CandidateHardSkillPersistenceMapper mapper;

    public CandidateHardSkillRepositoryAdapter(
            CandidateHardSkillJpaRepository jpaRepository,
            CandidateHardSkillPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public List<CandidateHardSkill> findByCandidateId(Long candidateId) {
        return jpaRepository.findByCandidateId(candidateId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public CandidateHardSkill save(CandidateHardSkill hardSkill) {
        var entity = mapper.toEntity(hardSkill);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public void deleteByCandidateId(Long candidateId) {
        jpaRepository.deleteByCandidateId(candidateId);
    }
}
