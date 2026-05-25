package com.ats.candidate.infrastructure.out.adapter;

import com.ats.candidate.domain.model.CandidateSoftSkill;
import com.ats.candidate.domain.port.out.repository.CandidateSoftSkillRepositoryPort;
import com.ats.candidate.infrastructure.out.mapper.CandidateSoftSkillPersistenceMapper;
import com.ats.candidate.infrastructure.out.repository.CandidateSoftSkillJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
public class CandidateSoftSkillRepositoryAdapter implements CandidateSoftSkillRepositoryPort {

    private final CandidateSoftSkillJpaRepository softSkillJpaRepository;
    private final CandidateSoftSkillPersistenceMapper softSkillPersistenceMapper;

    public CandidateSoftSkillRepositoryAdapter(
            CandidateSoftSkillJpaRepository softSkillJpaRepository,
            CandidateSoftSkillPersistenceMapper softSkillPersistenceMapper
    ) {
        this.softSkillJpaRepository = softSkillJpaRepository;
        this.softSkillPersistenceMapper = softSkillPersistenceMapper;
    }

    @Override
    public Set<CandidateSoftSkill> saveAll(Set<CandidateSoftSkill> softSkills) {
        return new HashSet<>(softSkillJpaRepository.saveAll(softSkills.stream()
                .map(softSkillPersistenceMapper::toEntity)
                .toList())
                .stream()
                .map(softSkillPersistenceMapper::toDomain)
                .toList());
    }

    @Override
    public List<CandidateSoftSkill> findByCandidateId(Long candidateId) {
        return softSkillJpaRepository.findByCandidateId(candidateId)
                .stream()
                .map(softSkillPersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public void deleteAllByCandidateId(Long candidateId) {
        softSkillJpaRepository.deleteAllByCandidateId(candidateId);
    }
}

