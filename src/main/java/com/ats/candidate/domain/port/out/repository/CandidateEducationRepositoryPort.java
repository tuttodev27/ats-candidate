package com.ats.candidate.domain.port.out.repository;

import com.ats.candidate.domain.model.CandidateEducation;

import java.util.List;
import java.util.Set;

public interface CandidateEducationRepositoryPort {
    Set<CandidateEducation> saveAll(Set<CandidateEducation> educations);
    List<CandidateEducation> findByCandidateId(Long candidateId);
    void deleteAllByCandidateId(Long candidateId);
}

