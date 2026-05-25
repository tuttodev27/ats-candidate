package com.ats.candidate.domain.port.out.repository;

import com.ats.candidate.domain.model.CandidateNote;

import java.util.List;
import java.util.Set;

public interface CandidateNoteRepositoryPort {
    Set<CandidateNote> saveAll(Set<CandidateNote> notes);
    List<CandidateNote> findByCandidateId(Long candidateId);
    void deleteAllByCandidateId(Long candidateId);
}
