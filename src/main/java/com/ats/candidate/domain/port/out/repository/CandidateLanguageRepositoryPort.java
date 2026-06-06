package com.ats.candidate.domain.port.out.repository;

import com.ats.candidate.domain.model.CandidateLanguage;

import java.util.List;
import java.util.Set;

public interface CandidateLanguageRepositoryPort {
    Set<CandidateLanguage> saveAll(Set<CandidateLanguage> languages);
    List<CandidateLanguage> findByCandidateId(Long candidateId);
    void deleteAllByCandidateId(Long candidateId);
}

