package com.ats.candidate.infrastructure.out.repository;

import com.ats.candidate.infrastructure.out.entity.EducationLevelEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EducationLevelJpaRepository extends JpaRepository<EducationLevelEntity, Long> {
    boolean existsByIdAndActiveTrue(Long id);
}
