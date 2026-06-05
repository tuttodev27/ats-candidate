package com.ats.candidate.domain.port.out.repository;

import com.ats.candidate.domain.model.CandidateHardSkill;

import java.util.List;

public interface CandidateHardSkillRepositoryPort {
    List<CandidateHardSkill> findByCandidateId(Long candidateId);
    CandidateHardSkill save(CandidateHardSkill hardSkill);
    void deleteByCandidateId(Long candidateId);
}
