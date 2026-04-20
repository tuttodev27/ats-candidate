package com.ats.candidate.infrastructure.out.repository;

import com.ats.candidate.infrastructure.out.entity.CandidateEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CandidateJpaRepository extends JpaRepository<CandidateEntity, Long> {
    boolean existsByEmailIgnoreCase(String email);

}
