package com.ats.candidate.infrastructure.out.repository;

import com.ats.candidate.infrastructure.out.entity.HardSkillEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HardSkillJpaRepository extends JpaRepository<HardSkillEntity, Long> {
    boolean existsByIdAndActiveTrue(Long id);
    java.util.Optional<HardSkillEntity> findByNameIgnoreCaseAndActiveTrue(String name);
}
