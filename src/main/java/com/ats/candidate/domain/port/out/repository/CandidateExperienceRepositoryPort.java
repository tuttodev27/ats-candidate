package com.ats.candidate.domain.port.out.repository;

import com.ats.candidate.domain.model.CandidateExperience;

import java.util.List;
import java.util.Set;

public interface CandidateExperienceRepositoryPort {
    Set<CandidateExperience> saveAll(Set<CandidateExperience> experiences);
    List<CandidateExperience> findByCandidateId(Long candidateId);
    void deleteAllByCandidateId(Long candidateId);
}
