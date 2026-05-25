package com.ats.candidate.infrastructure.out.repository;

import com.ats.candidate.infrastructure.out.entity.CandidateExperienceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CandidateExperienceJpaRepository extends JpaRepository<CandidateExperienceEntity, Long> {
    List<CandidateExperienceEntity> findByCandidateId(Long candidateId);
    void deleteAllByCandidateId(Long candidateId);
}
