package com.ats.candidate.domain.port.out.repository;

import java.util.Set;

public interface CandidateCatalogValidationPort {

    boolean existsHardSkill(Long hardSkillId);

    Set<Long> findExistingHardSkillIds(Set<Long> ids);

    boolean existsSoftSkill(Long softSkillId);

    Set<Long> findExistingSoftSkillIds(Set<Long> ids);

    boolean existsEducationLevel(Long educationLevelId);
}
