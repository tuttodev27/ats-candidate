package com.ats.candidate.infrastructure.out.adapter;

import com.ats.candidate.domain.port.out.repository.CandidateCatalogValidationPort;
import com.ats.candidate.infrastructure.out.repository.EducationLevelJpaRepository;
import com.ats.candidate.infrastructure.out.repository.HardSkillJpaRepository;
import com.ats.candidate.infrastructure.out.repository.SoftSkillJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public class CandidateCatalogValidationAdapter implements CandidateCatalogValidationPort {

    private final HardSkillJpaRepository hardSkillJpaRepository;
    private final SoftSkillJpaRepository softSkillJpaRepository;
    private final EducationLevelJpaRepository educationLevelJpaRepository;

    public CandidateCatalogValidationAdapter(
            HardSkillJpaRepository hardSkillJpaRepository,
            SoftSkillJpaRepository softSkillJpaRepository,
            EducationLevelJpaRepository educationLevelJpaRepository) {
        this.hardSkillJpaRepository = hardSkillJpaRepository;
        this.softSkillJpaRepository = softSkillJpaRepository;
        this.educationLevelJpaRepository = educationLevelJpaRepository;
    }

    @Override
    public boolean existsHardSkill(Long hardSkillId) {
        return hardSkillJpaRepository.existsByIdAndActiveTrue(hardSkillId);
    }

    @Override
    public Set<Long> findExistingHardSkillIds(Set<Long> ids) {
        return ids.stream()
                .filter(this::existsHardSkill)
                .collect(java.util.stream.Collectors.toSet());
    }

    @Override
    public boolean existsSoftSkill(Long softSkillId) {
        return softSkillJpaRepository.existsByIdAndActiveTrue(softSkillId);
    }

    @Override
    public Set<Long> findExistingSoftSkillIds(Set<Long> ids) {
        return ids.stream()
                .filter(this::existsSoftSkill)
                .collect(java.util.stream.Collectors.toSet());
    }

    @Override
    public boolean existsEducationLevel(Long educationLevelId) {
        return educationLevelJpaRepository.existsByIdAndActiveTrue(educationLevelId);
    }
}
