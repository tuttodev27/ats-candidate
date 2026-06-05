package com.ats.candidate.infrastructure.out.repository;

import com.ats.candidate.infrastructure.out.entity.CandidateProfessionalProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CandidateProfessionalProfileJpaRepository extends JpaRepository<CandidateProfessionalProfileEntity, Long> {
    Optional<CandidateProfessionalProfileEntity> findByCandidateId(Long candidateId);
    void deleteByCandidateId(Long candidateId);
}
