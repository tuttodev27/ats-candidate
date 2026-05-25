package com.ats.candidate.infrastructure.out.repository;

import com.ats.candidate.infrastructure.out.entity.CandidateSoftSkillEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CandidateSoftSkillJpaRepository extends JpaRepository<CandidateSoftSkillEntity, Long> {
    List<CandidateSoftSkillEntity> findByCandidateId(Long candidateId);
    void deleteAllByCandidateId(Long candidateId);
}

