package com.ats.candidate.infrastructure.out.repository;

import com.ats.candidate.infrastructure.out.entity.SoftSkillEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SoftSkillJpaRepository extends JpaRepository<SoftSkillEntity, Long> {
    boolean existsByIdAndActiveTrue(Long id);
    java.util.Optional<SoftSkillEntity> findByNameIgnoreCaseAndActiveTrue(String name);
}
