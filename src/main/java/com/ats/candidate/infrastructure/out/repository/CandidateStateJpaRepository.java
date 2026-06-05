package com.ats.candidate.infrastructure.out.repository;

import com.ats.candidate.infrastructure.out.entity.CandidateStateEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CandidateStateJpaRepository extends JpaRepository<CandidateStateEntity, Long> {
    List<CandidateStateEntity> findByCandidateIdOrderByCreatedAtDesc(Long candidateId);
    void deleteByCandidateId(Long candidateId);
}
