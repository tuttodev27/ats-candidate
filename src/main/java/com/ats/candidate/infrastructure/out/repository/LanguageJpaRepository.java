package com.ats.candidate.infrastructure.out.repository;

import com.ats.candidate.infrastructure.out.entity.LanguageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LanguageJpaRepository extends JpaRepository<LanguageEntity, Long> {
    List<LanguageEntity> findByActiveTrueOrderByNameAsc();
    boolean existsByIdAndActiveTrue(Long id);
}
