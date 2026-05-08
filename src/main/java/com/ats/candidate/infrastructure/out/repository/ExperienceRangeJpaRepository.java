package com.ats.candidate.infrastructure.out.repository;

import com.ats.candidate.infrastructure.out.entity.ExperienceRangeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExperienceRangeJpaRepository extends JpaRepository<ExperienceRangeEntity, Long> {
    List<ExperienceRangeEntity> findByActiveTrueOrderByOrderNumberAsc();
    boolean existsByIdAndActiveTrue(Long id);
}
