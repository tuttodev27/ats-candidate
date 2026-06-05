package com.ats.candidate.domain.port.out.repository;

import com.ats.candidate.domain.model.CandidateEducation;

import java.util.List;

public interface CandidateEducationRepositoryPort {
    List<CandidateEducation> findByCandidateId(Long candidateId);
    CandidateEducation save(CandidateEducation education);
    void deleteByCandidateId(Long candidateId);
}
