package com.ats.candidate.infrastructure.out.repository;

import com.ats.candidate.infrastructure.out.entity.CandidateParseResultEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CandidateParseResultJpaRepository extends JpaRepository<CandidateParseResultEntity, Long> {
}
