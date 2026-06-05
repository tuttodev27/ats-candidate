package com.ats.candidate.infrastructure.out.repository;

import com.ats.candidate.infrastructure.out.entity.CandidateEducationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CandidateEducationJpaRepository extends JpaRepository<CandidateEducationEntity, Long> {
    List<CandidateEducationEntity> findByCandidateId(Long candidateId);
    void deleteByCandidateId(Long candidateId);
}
