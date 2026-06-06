package com.ats.candidate.domain.port.out.repository;

import com.ats.candidate.domain.model.CandidateProfessionalProfile;

import java.util.Optional;

public interface CandidateProfessionalProfileRepositoryPort {
    CandidateProfessionalProfile save(CandidateProfessionalProfile professionalProfile);
    Optional<CandidateProfessionalProfile> findByCandidateId(Long candidateId);
}
