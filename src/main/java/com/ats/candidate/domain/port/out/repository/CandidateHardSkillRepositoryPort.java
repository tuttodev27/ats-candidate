package com.ats.candidate.domain.port.out.repository;

import com.ats.candidate.domain.model.CandidateHardSkill;

import java.util.List;
import java.util.Set;

public interface CandidateHardSkillRepositoryPort {
    Set<CandidateHardSkill> saveAll(Set<CandidateHardSkill> hardSkills);
    List<CandidateHardSkill> findByCandidateId(Long candidateId);
    void deleteAllByCandidateId(Long candidateId);
}

