package com.ats.candidate.infrastructure.out.repository;

import com.ats.candidate.infrastructure.out.entity.EducationLevelEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EducationLevelJpaRepository extends JpaRepository<EducationLevelEntity, Long> {
    List<EducationLevelEntity> findByActiveTrueOrderByOrderNumberAsc();
    boolean existsByIdAndActiveTrue(Long id);
}
