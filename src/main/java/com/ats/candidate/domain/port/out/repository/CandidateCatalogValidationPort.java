package com.ats.candidate.domain.port.out.repository;

import java.util.List;

public interface CandidateCatalogValidationPort {
    boolean existsActiveCountryCode(String isoCode);
    boolean existsActiveExperienceRange(Long id);
    boolean existsActiveEducationLevel(Long id);
    boolean existsActiveLanguage(Long id);
    boolean existsActiveLanguageLevel(Long id);
    boolean existsActiveHardSkill(Long id);
    boolean existsActiveSoftSkill(Long id);
    List<String> getActiveHardSkillNames();
    List<String> getActiveSoftSkillNames();
    java.util.Optional<Long> findHardSkillIdByName(String name);
    java.util.Optional<Long> findSoftSkillIdByName(String name);
}
