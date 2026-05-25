package com.ats.candidate.infrastructure.out.adapter;

import com.ats.candidate.domain.model.CandidateHardSkill;
import com.ats.candidate.domain.port.out.repository.CandidateHardSkillRepositoryPort;
import com.ats.candidate.infrastructure.out.mapper.CandidateHardSkillPersistenceMapper;
import com.ats.candidate.infrastructure.out.repository.CandidateHardSkillJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
public class CandidateHardSkillRepositoryAdapter implements CandidateHardSkillRepositoryPort {

    private final CandidateHardSkillJpaRepository hardSkillJpaRepository;
    private final CandidateHardSkillPersistenceMapper hardSkillPersistenceMapper;

    public CandidateHardSkillRepositoryAdapter(
            CandidateHardSkillJpaRepository hardSkillJpaRepository,
            CandidateHardSkillPersistenceMapper hardSkillPersistenceMapper
    ) {
        this.hardSkillJpaRepository = hardSkillJpaRepository;
        this.hardSkillPersistenceMapper = hardSkillPersistenceMapper;
    }

    @Override
    public Set<CandidateHardSkill> saveAll(Set<CandidateHardSkill> hardSkills) {
        return new HashSet<>(hardSkillJpaRepository.saveAll(hardSkills.stream()
                .map(hardSkillPersistenceMapper::toEntity)
                .toList())
                .stream()
                .map(hardSkillPersistenceMapper::toDomain)
                .toList());
    }

    @Override
    public List<CandidateHardSkill> findByCandidateId(Long candidateId) {
        return hardSkillJpaRepository.findByCandidateId(candidateId)
                .stream()
                .map(hardSkillPersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public void deleteAllByCandidateId(Long candidateId) {
        hardSkillJpaRepository.deleteAllByCandidateId(candidateId);
    }
}

