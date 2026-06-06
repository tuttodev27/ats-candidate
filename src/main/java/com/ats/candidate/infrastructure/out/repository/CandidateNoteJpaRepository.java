package com.ats.candidate.infrastructure.out.repository;

import com.ats.candidate.infrastructure.out.entity.CandidateNoteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CandidateNoteJpaRepository extends JpaRepository<CandidateNoteEntity, Long> {
    List<CandidateNoteEntity> findByCandidateId(Long candidateId);
    void deleteAllByCandidateId(Long candidateId);
}
