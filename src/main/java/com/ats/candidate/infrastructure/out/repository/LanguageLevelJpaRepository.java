package com.ats.candidate.infrastructure.out.repository;

import com.ats.candidate.infrastructure.out.entity.LanguageLevelEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LanguageLevelJpaRepository extends JpaRepository<LanguageLevelEntity, Long> {
    List<LanguageLevelEntity> findByActiveTrueOrderByOrderNumberAsc();
    boolean existsByIdAndActiveTrue(Long id);
}
