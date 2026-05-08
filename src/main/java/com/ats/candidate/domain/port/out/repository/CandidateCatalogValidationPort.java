package com.ats.candidate.domain.port.out.repository;

public interface CandidateCatalogValidationPort {
    boolean existsActiveCountryCode(String isoCode);
    boolean existsActiveExperienceRange(Long id);
    boolean existsActiveEducationLevel(Long id);
    boolean existsActiveLanguage(Long id);
    boolean existsActiveLanguageLevel(Long id);
    boolean existsActiveHardSkill(Long id);
    boolean existsActiveSoftSkill(Long id);
}
