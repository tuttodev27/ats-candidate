package com.ats.candidate.infrastructure.out.repository;

import com.ats.candidate.infrastructure.out.entity.CandidateStateEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CandidateStateJpaRepository extends JpaRepository<CandidateStateEntity, Long> {
    Optional<CandidateStateEntity> findTopByCandidateIdOrderByCreatedAtDesc(Long candidateId);
    List<CandidateStateEntity> findByCandidateIdOrderByCreatedAtAsc(Long candidateId);
}

