package com.ats.candidate.infrastructure.out.repository;

import com.ats.candidate.infrastructure.out.entity.CandidateLanguageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CandidateLanguageJpaRepository extends JpaRepository<CandidateLanguageEntity, Long> {
    List<CandidateLanguageEntity> findByCandidateId(Long candidateId);
}
