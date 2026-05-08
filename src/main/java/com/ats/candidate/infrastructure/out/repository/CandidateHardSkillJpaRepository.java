package com.ats.candidate.infrastructure.out.repository;

import com.ats.candidate.infrastructure.out.entity.CandidateHardSkillEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CandidateHardSkillJpaRepository extends JpaRepository<CandidateHardSkillEntity, Long> {
    List<CandidateHardSkillEntity> findByCandidateId(Long candidateId);
}
