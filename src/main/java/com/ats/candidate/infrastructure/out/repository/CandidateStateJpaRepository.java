package com.ats.candidate.infrastructure.out.repository;

import com.ats.candidate.infrastructure.out.entity.CandidateStateEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CandidateStateJpaRepository extends JpaRepository<CandidateStateEntity, Long> {
}
