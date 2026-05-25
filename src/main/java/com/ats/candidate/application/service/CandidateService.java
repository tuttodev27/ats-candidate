package com.ats.candidate.application.service;

import com.ats.candidate.domain.exception.CandidateNotFoundException;
import com.ats.candidate.domain.exception.EmailAlreadyExistException;
import com.ats.candidate.domain.exception.InvalidCatalogReferenceException;
import com.ats.candidate.domain.model.Candidate;
import com.ats.candidate.domain.model.CandidateEducation;
import com.ats.candidate.domain.model.CandidateExperience;
import com.ats.candidate.domain.model.CandidateHardSkill;
import com.ats.candidate.domain.model.CandidateLanguage;
import com.ats.candidate.domain.model.CandidateNote;
import com.ats.candidate.domain.model.CandidateProfessionalProfile;
import com.ats.candidate.domain.model.CandidateSoftSkill;
import com.ats.candidate.domain.model.CandidateState;
import com.ats.candidate.domain.port.in.usecase.CandidateUseCase;
import com.ats.candidate.domain.port.out.repository.AttachmentRepositoryPort;
import com.ats.candidate.domain.port.out.repository.CandidateCatalogValidationPort;
import com.ats.candidate.domain.port.out.repository.CandidateEducationRepositoryPort;
import com.ats.candidate.domain.port.out.repository.CandidateExperienceRepositoryPort;
import com.ats.candidate.domain.port.out.repository.CandidateHardSkillRepositoryPort;
import com.ats.candidate.domain.port.out.repository.CandidateLanguageRepositoryPort;
import com.ats.candidate.domain.port.out.repository.CandidateNoteRepositoryPort;
import com.ats.candidate.domain.port.out.repository.CandidateProfessionalProfileRepositoryPort;
import com.ats.candidate.domain.port.out.repository.CandidateRepositoryPort;
import com.ats.candidate.domain.port.out.repository.CandidateSoftSkillRepositoryPort;
import com.ats.candidate.domain.port.out.repository.CandidateStateRepositoryPort;


import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.time.LocalDateTime;


@Service
public class CandidateService implements CandidateUseCase {

    private static final String INITIAL_STATE = "NEW";

    private final CandidateRepositoryPort candidateRepositoryPort;
    private final CandidateCatalogValidationPort catalogValidationPort;
    private final CandidateProfessionalProfileRepositoryPort professionalProfileRepositoryPort;
    private final CandidateEducationRepositoryPort educationRepositoryPort;
    private final CandidateLanguageRepositoryPort languageRepositoryPort;
    private final CandidateHardSkillRepositoryPort hardSkillRepositoryPort;
    private final CandidateSoftSkillRepositoryPort softSkillRepositoryPort;
    private final CandidateStateRepositoryPort candidateStateRepositoryPort;
    private final AttachmentRepositoryPort attachmentRepositoryPort;
    private final CandidateExperienceRepositoryPort candidateExperienceRepositoryPort;
    private final CandidateNoteRepositoryPort candidateNoteRepositoryPort;

    public CandidateService(
            CandidateRepositoryPort candidateRepositoryPort,
            CandidateCatalogValidationPort catalogValidationPort,
            CandidateProfessionalProfileRepositoryPort professionalProfileRepositoryPort,
            CandidateEducationRepositoryPort educationRepositoryPort,
            CandidateLanguageRepositoryPort languageRepositoryPort,
            CandidateHardSkillRepositoryPort hardSkillRepositoryPort,
            CandidateSoftSkillRepositoryPort softSkillRepositoryPort,
            CandidateStateRepositoryPort candidateStateRepositoryPort,
            AttachmentRepositoryPort attachmentRepositoryPort,
            CandidateExperienceRepositoryPort candidateExperienceRepositoryPort,
            CandidateNoteRepositoryPort candidateNoteRepositoryPort
    ) {
        this.candidateRepositoryPort = candidateRepositoryPort;
        this.catalogValidationPort = catalogValidationPort;
        this.professionalProfileRepositoryPort = professionalProfileRepositoryPort;
        this.educationRepositoryPort = educationRepositoryPort;
        this.languageRepositoryPort = languageRepositoryPort;
        this.hardSkillRepositoryPort = hardSkillRepositoryPort;
        this.softSkillRepositoryPort = softSkillRepositoryPort;
        this.candidateStateRepositoryPort = candidateStateRepositoryPort;
        this.attachmentRepositoryPort = attachmentRepositoryPort;
        this.candidateExperienceRepositoryPort = candidateExperienceRepositoryPort;
        this.candidateNoteRepositoryPort = candidateNoteRepositoryPort;
    }


    @Override
    @Transactional
    public Candidate create(Candidate candidate, Long recruiterId) {
        if (candidateRepositoryPort.existsByEmail(candidate.getEmail())) {
            throw new EmailAlreadyExistException(candidate.getEmail());
        }
        LocalDateTime now = LocalDateTime.now();
        validateCatalogReferences(candidate);
        candidate.setActive(true);
        candidate.setCreatedBy(recruiterId);
        candidate.setCreatedAt(now);
        prepareDetails(candidate, recruiterId, now);

        Candidate saved = candidateRepositoryPort.save(candidate);
        saveDetails(candidate, saved.getId());
        saveInitialState(saved.getId(), recruiterId, now);
        return loadDetails(saved);
    }

    @Override
    @Transactional
    public Candidate update(Long id, Candidate candidate, Long recruiterId) {
        if (!candidateRepositoryPort.existsById(id)) {
            throw new CandidateNotFoundException(id);
        }

        validateCatalogReferences(candidate);

        LocalDateTime now = LocalDateTime.now();
        candidate.setId(id);
        candidate.setUpdatedAt(now);
        candidate.setUpdatedBy(recruiterId);

        prepareDetails(candidate, recruiterId, now);

        Candidate updated = candidateRepositoryPort.update(candidate);

        educationRepositoryPort.deleteAllByCandidateId(id);
        languageRepositoryPort.deleteAllByCandidateId(id);
        hardSkillRepositoryPort.deleteAllByCandidateId(id);
        softSkillRepositoryPort.deleteAllByCandidateId(id);
        candidateExperienceRepositoryPort.deleteAllByCandidateId(id);
        candidateNoteRepositoryPort.deleteAllByCandidateId(id);

        saveDetails(candidate, id);

        return loadDetails(updated);
    }


    @Override
    public Page<Candidate> list(Boolean active, Pageable pageable) {
        return candidateRepositoryPort.findAll(active, pageable)
                .map(this::loadDetails);
    }

    @Override
    public Candidate getById(Long id) {
        return candidateRepositoryPort.findById(id)
                .map(this::loadDetails)
                .orElseThrow(()-> new CandidateNotFoundException(id));
    }

    private void saveDetails(Candidate candidate, Long candidateId) {
        if (candidate.getProfessionalProfile() != null) {
            candidate.getProfessionalProfile().setCandidateId(candidateId);
            professionalProfileRepositoryPort.save(candidate.getProfessionalProfile());
        }
        if (candidate.getEducations() != null && !candidate.getEducations().isEmpty()) {
            candidate.getEducations().forEach(education -> education.setCandidateId(candidateId));
            educationRepositoryPort.saveAll(candidate.getEducations());
        }
        if (candidate.getLanguages() != null && !candidate.getLanguages().isEmpty()) {
            candidate.getLanguages().forEach(language -> language.setCandidateId(candidateId));
            languageRepositoryPort.saveAll(candidate.getLanguages());
        }
        if (candidate.getHardSkills() != null && !candidate.getHardSkills().isEmpty()) {
            candidate.getHardSkills().forEach(hardSkill -> hardSkill.setCandidateId(candidateId));
            hardSkillRepositoryPort.saveAll(candidate.getHardSkills());
        }
        if (candidate.getSoftSkills() != null && !candidate.getSoftSkills().isEmpty()) {
            candidate.getSoftSkills().forEach(softSkill -> softSkill.setCandidateId(candidateId));
            softSkillRepositoryPort.saveAll(candidate.getSoftSkills());
        }
        if (candidate.getExperiences() != null && !candidate.getExperiences().isEmpty()) {
            candidate.getExperiences().forEach(experience -> experience.setCandidateId(candidateId));
            candidateExperienceRepositoryPort.saveAll(candidate.getExperiences());
        }
        if (candidate.getNotes() != null && !candidate.getNotes().isEmpty()) {
            candidate.getNotes().forEach(note -> note.setCandidateId(candidateId.intValue()));
            candidateNoteRepositoryPort.saveAll(candidate.getNotes());
        }
    }


    private void saveInitialState(Long candidateId, Long recruiterId, LocalDateTime now) {
        candidateStateRepositoryPort.save(CandidateState.builder()
                .candidateId(candidateId)
                .state(INITIAL_STATE)
                .createdAt(now)
                .createdBy(recruiterId)
                .build());
    }

    private void validateCatalogReferences(Candidate candidate) {
        validateCountryCode(candidate.getCountryCode());
        validateProfessionalProfile(candidate.getProfessionalProfile());
        validateEducations(candidate);
        validateLanguages(candidate);
        validateHardSkills(candidate);
        validateSoftSkills(candidate);
        validateExperiences(candidate);
    }

    private void validateExperiences(Candidate candidate) {
        if (candidate.getExperiences() == null) {
            return;
        }
        for (CandidateExperience experience : candidate.getExperiences()) {
            if (experience.getStartDate() != null && experience.getEndDate() != null
                    && experience.getStartDate().isAfter(experience.getEndDate())) {
                throw new InvalidCatalogReferenceException("Experience start date must be before or equal to end date");
            }
        }
    }


    private void validateCountryCode(String countryCode) {
        if (countryCode != null && !countryCode.isBlank() && !catalogValidationPort.existsActiveCountryCode(countryCode)) {
            throw new InvalidCatalogReferenceException("Invalid or inactive country code: " + countryCode);
        }
    }

    private void validateProfessionalProfile(CandidateProfessionalProfile professionalProfile) {
        if (professionalProfile == null || professionalProfile.getExperienceRangeId() == null) {
            return;
        }
        Long experienceRangeId = professionalProfile.getExperienceRangeId();
        if (!catalogValidationPort.existsActiveExperienceRange(experienceRangeId)) {
            throw new InvalidCatalogReferenceException("Invalid or inactive experienceRangeId: " + experienceRangeId);
        }
    }

    private void validateEducations(Candidate candidate) {
        if (candidate.getEducations() == null) {
            return;
        }
        for (CandidateEducation education : candidate.getEducations()) {
            Long id = education.getEducationLevelId();
            if (id != null && !catalogValidationPort.existsActiveEducationLevel(id)) {
                throw new InvalidCatalogReferenceException("Invalid or inactive educationLevelId: " + id);
            }
            if (education.getStartDate() != null && education.getEndDate() != null
                    && education.getStartDate().isAfter(education.getEndDate())) {
                throw new InvalidCatalogReferenceException("Education start date must be before or equal to end date");
            }
        }
    }


    private void validateLanguages(Candidate candidate) {
        if (candidate.getLanguages() == null) {
            return;
        }
        candidate.getLanguages().forEach(language -> {
            Long languageId = language.getLanguageId();
            if (languageId != null && !catalogValidationPort.existsActiveLanguage(languageId)) {
                throw new InvalidCatalogReferenceException("Invalid or inactive languageId: " + languageId);
            }
            Long languageLevelId = language.getLanguageLevelId();
            if (languageLevelId != null && !catalogValidationPort.existsActiveLanguageLevel(languageLevelId)) {
                throw new InvalidCatalogReferenceException("Invalid or inactive languageLevelId: " + languageLevelId);
            }
        });
    }

    private void validateHardSkills(Candidate candidate) {
        if (candidate.getHardSkills() == null) {
            return;
        }
        candidate.getHardSkills().stream()
                .map(CandidateHardSkill::getHardSkillId)
                .filter(id -> id != null && !catalogValidationPort.existsActiveHardSkill(id))
                .findFirst()
                .ifPresent(id -> {
                    throw new InvalidCatalogReferenceException("Invalid or inactive hardSkillId: " + id);
                });
    }

    private void validateSoftSkills(Candidate candidate) {
        if (candidate.getSoftSkills() == null) {
            return;
        }
        candidate.getSoftSkills().stream()
                .map(CandidateSoftSkill::getSoftSkillId)
                .filter(id -> id != null && !catalogValidationPort.existsActiveSoftSkill(id))
                .findFirst()
                .ifPresent(id -> {
                    throw new InvalidCatalogReferenceException("Invalid or inactive softSkillId: " + id);
                });
    }

    private Candidate loadDetails(Candidate candidate) {
        Long candidateId = candidate.getId();
        if (candidateId == null) {
            return candidate;
        }

        professionalProfileRepositoryPort.findByCandidateId(candidateId)
                .ifPresent(candidate::setProfessionalProfile);
        candidate.setEducations(new HashSet<>(educationRepositoryPort.findByCandidateId(candidateId)));
        candidate.setLanguages(new HashSet<>(languageRepositoryPort.findByCandidateId(candidateId)));
        candidate.setHardSkills(new HashSet<>(hardSkillRepositoryPort.findByCandidateId(candidateId)));
        candidate.setSoftSkills(new HashSet<>(softSkillRepositoryPort.findByCandidateId(candidateId)));
        candidate.setAttachments(new HashSet<>(attachmentRepositoryPort.findByCandidateId(candidateId)));
        candidate.setExperiences(new HashSet<>(candidateExperienceRepositoryPort.findByCandidateId(candidateId)));
        candidate.setNotes(new HashSet<>(candidateNoteRepositoryPort.findByCandidateId(candidateId)));
        candidateStateRepositoryPort.findLatestByCandidateId(candidateId)
                .ifPresent(state -> candidate.setStates(Set.of(state)));
        return candidate;
    }



    private void prepareDetails(Candidate candidate, Long recruiterId, LocalDateTime now) {
        CandidateProfessionalProfile professionalProfile = candidate.getProfessionalProfile();
        if (professionalProfile != null) {
            professionalProfile.setCreatedAt(now);
            professionalProfile.setUpdatedAt(now);
        }

        if (candidate.getEducations() != null) {
            candidate.getEducations().forEach(education -> prepareEducation(education, now));
        }
        if (candidate.getLanguages() != null) {
            candidate.getLanguages().forEach(language -> prepareLanguage(language, now));
        }
        if (candidate.getHardSkills() != null) {
            candidate.getHardSkills().forEach(hardSkill -> prepareHardSkill(hardSkill, now));
        }
        if (candidate.getSoftSkills() != null) {
            candidate.getSoftSkills().forEach(softSkill -> prepareSoftSkill(softSkill, now));
        }
        if (candidate.getExperiences() != null) {
            candidate.getExperiences().forEach(experience -> prepareExperience(experience, now));
        }
        if (candidate.getNotes() != null) {
            candidate.getNotes().forEach(note -> prepareNote(note, recruiterId, now));
        }
    }

    private void prepareEducation(CandidateEducation education, LocalDateTime now) {
        education.setCreatedAt(now);
        education.setUpdatedAt(now);
    }

    private void prepareLanguage(CandidateLanguage language, LocalDateTime now) {
        language.setCreatedAt(now);
    }

    private void prepareHardSkill(CandidateHardSkill hardSkill, LocalDateTime now) {
        hardSkill.setCreatedAt(now);
    }

    private void prepareSoftSkill(CandidateSoftSkill softSkill, LocalDateTime now) {
        softSkill.setCreatedAt(now);
    }

    private void prepareExperience(CandidateExperience experience, LocalDateTime now) {
        experience.setCreatedAt(now);
        experience.setUpdatedAt(now);
    }

    private void prepareNote(CandidateNote note, Long recruiterId, LocalDateTime now) {
        note.setCreatedBy(String.valueOf(recruiterId));
        note.setCreatedAt(now.toString());
    }
}

