package com.ats.candidate.domain.port.out.repository;

import com.ats.candidate.domain.model.CandidateSoftSkill;

import java.util.List;

public interface CandidateSoftSkillRepositoryPort {
    List<CandidateSoftSkill> findByCandidateId(Long candidateId);
    CandidateSoftSkill save(CandidateSoftSkill softSkill);
    void deleteByCandidateId(Long candidateId);
}
