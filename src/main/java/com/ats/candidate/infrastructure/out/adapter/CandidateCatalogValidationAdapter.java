package com.ats.candidate.infrastructure.out.adapter;

import com.ats.candidate.domain.port.out.repository.CandidateCatalogValidationPort;
import com.ats.candidate.infrastructure.out.repository.CountryCodeJpaRepository;
import com.ats.candidate.infrastructure.out.repository.EducationLevelJpaRepository;
import com.ats.candidate.infrastructure.out.repository.ExperienceRangeJpaRepository;
import com.ats.candidate.infrastructure.out.repository.HardSkillJpaRepository;
import com.ats.candidate.infrastructure.out.repository.LanguageJpaRepository;
import com.ats.candidate.infrastructure.out.repository.LanguageLevelJpaRepository;
import com.ats.candidate.infrastructure.out.repository.SoftSkillJpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public class CandidateCatalogValidationAdapter implements CandidateCatalogValidationPort {

    private final CountryCodeJpaRepository countryCodeJpaRepository;
    private final ExperienceRangeJpaRepository experienceRangeJpaRepository;
    private final EducationLevelJpaRepository educationLevelJpaRepository;
    private final LanguageJpaRepository languageJpaRepository;
    private final LanguageLevelJpaRepository languageLevelJpaRepository;
    private final HardSkillJpaRepository hardSkillJpaRepository;
    private final SoftSkillJpaRepository softSkillJpaRepository;

    public CandidateCatalogValidationAdapter(
            CountryCodeJpaRepository countryCodeJpaRepository,
            ExperienceRangeJpaRepository experienceRangeJpaRepository,
            EducationLevelJpaRepository educationLevelJpaRepository,
            LanguageJpaRepository languageJpaRepository,
            LanguageLevelJpaRepository languageLevelJpaRepository,
            HardSkillJpaRepository hardSkillJpaRepository,
            SoftSkillJpaRepository softSkillJpaRepository
    ) {
        this.countryCodeJpaRepository = countryCodeJpaRepository;
        this.experienceRangeJpaRepository = experienceRangeJpaRepository;
        this.educationLevelJpaRepository = educationLevelJpaRepository;
        this.languageJpaRepository = languageJpaRepository;
        this.languageLevelJpaRepository = languageLevelJpaRepository;
        this.hardSkillJpaRepository = hardSkillJpaRepository;
        this.softSkillJpaRepository = softSkillJpaRepository;
    }

    @Override
    public boolean existsActiveCountryCode(String isoCode) {
        return isoCode != null && countryCodeJpaRepository.existsByIsoCodeIgnoreCaseAndActiveTrue(isoCode.trim());
    }

    @Override
    public boolean existsActiveExperienceRange(Long id) {
        return id != null && experienceRangeJpaRepository.existsByIdAndActiveTrue(id);
    }

    @Override
    public boolean existsActiveEducationLevel(Long id) {
        return id != null && educationLevelJpaRepository.existsByIdAndActiveTrue(id);
    }

    @Override
    public boolean existsActiveLanguage(Long id) {
        return id != null && languageJpaRepository.existsByIdAndActiveTrue(id);
    }

    @Override
    public boolean existsActiveLanguageLevel(Long id) {
        return id != null && languageLevelJpaRepository.existsByIdAndActiveTrue(id);
    }

    @Override
    public boolean existsActiveHardSkill(Long id) {
        return id != null && hardSkillJpaRepository.existsByIdAndActiveTrue(id);
    }

    @Override
    public boolean existsActiveSoftSkill(Long id) {
        return id != null && softSkillJpaRepository.existsByIdAndActiveTrue(id);
    }
}
