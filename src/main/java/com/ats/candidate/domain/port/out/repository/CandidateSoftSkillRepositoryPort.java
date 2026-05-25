package com.ats.candidate.domain.port.out.repository;

import com.ats.candidate.domain.model.CandidateSoftSkill;

import java.util.List;
import java.util.Set;

public interface CandidateSoftSkillRepositoryPort {
    Set<CandidateSoftSkill> saveAll(Set<CandidateSoftSkill> softSkills);
    List<CandidateSoftSkill> findByCandidateId(Long candidateId);
    void deleteAllByCandidateId(Long candidateId);
}

