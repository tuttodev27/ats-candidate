package com.ats.candidate.application.service;

import com.ats.candidate.domain.exception.EmailAlreadyExistException;
import com.ats.candidate.domain.exception.InvalidCatalogReferenceException;
import com.ats.candidate.domain.model.Candidate;
import com.ats.candidate.domain.model.CandidateEducation;
import com.ats.candidate.domain.model.CandidateHardSkill;
import com.ats.candidate.domain.model.CandidateProfessionalProfile;
import com.ats.candidate.domain.model.CandidateSoftSkill;
import com.ats.candidate.domain.model.CandidateState;
import com.ats.candidate.domain.port.in.usecase.CandidateUseCase;
import com.ats.candidate.domain.port.out.repository.CandidateCatalogValidationPort;
import com.ats.candidate.domain.port.out.repository.CandidateEducationRepositoryPort;
import com.ats.candidate.domain.port.out.repository.CandidateHardSkillRepositoryPort;
import com.ats.candidate.domain.port.out.repository.CandidateProfessionalProfileRepositoryPort;
import com.ats.candidate.domain.port.out.repository.CandidateRepositoryPort;
import com.ats.candidate.domain.port.out.repository.CandidateSoftSkillRepositoryPort;
import com.ats.candidate.domain.port.out.repository.CandidateStateRepositoryPort;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

@Service
public class CandidateService implements CandidateUseCase {

    private final CandidateRepositoryPort candidateRepositoryPort;
    private final CandidateCatalogValidationPort catalogValidationPort;
    private final CandidateProfessionalProfileRepositoryPort professionalProfileRepositoryPort;
    private final CandidateEducationRepositoryPort educationRepositoryPort;
    private final CandidateHardSkillRepositoryPort hardSkillRepositoryPort;
    private final CandidateSoftSkillRepositoryPort softSkillRepositoryPort;
    private final CandidateStateRepositoryPort stateRepositoryPort;

    public CandidateService(
            CandidateRepositoryPort candidateRepositoryPort,
            CandidateCatalogValidationPort catalogValidationPort,
            CandidateProfessionalProfileRepositoryPort professionalProfileRepositoryPort,
            CandidateEducationRepositoryPort educationRepositoryPort,
            CandidateHardSkillRepositoryPort hardSkillRepositoryPort,
            CandidateSoftSkillRepositoryPort softSkillRepositoryPort,
            CandidateStateRepositoryPort stateRepositoryPort) {
        this.candidateRepositoryPort = candidateRepositoryPort;
        this.catalogValidationPort = catalogValidationPort;
        this.professionalProfileRepositoryPort = professionalProfileRepositoryPort;
        this.educationRepositoryPort = educationRepositoryPort;
        this.hardSkillRepositoryPort = hardSkillRepositoryPort;
        this.softSkillRepositoryPort = softSkillRepositoryPort;
        this.stateRepositoryPort = stateRepositoryPort;
    }

    @Override
    @Transactional
    public Candidate create(Candidate candidate, Long recruiterId) {
        if (candidateRepositoryPort.existsByEmail(candidate.getEmail())) {
            throw new EmailAlreadyExistException(candidate.getEmail());
        }

        validateCatalogReferences(candidate);

        candidate.setActive(true);
        candidate.setCreatedBy(recruiterId);
        candidate.setCreatedAt(LocalDateTime.now());

        Candidate saved = candidateRepositoryPort.save(candidate);
        Long candidateId = saved.getId();

        saveProfessionalProfile(candidate.getProfessionalProfile(), candidateId);
        saveEducations(candidate.getEducations(), candidateId);
        saveHardSkills(candidate.getHardSkills(), candidateId);
        saveSoftSkills(candidate.getSoftSkills(), candidateId);
        saveInitialState(candidateId, recruiterId);

        return loadFullCandidate(candidateId);
    }

    private void validateCatalogReferences(Candidate candidate) {
        if (candidate.getHardSkills() != null) {
            for (CandidateHardSkill hs : candidate.getHardSkills()) {
                if (hs.getHardSkillId() != null && !catalogValidationPort.existsHardSkill(hs.getHardSkillId())) {
                    throw new InvalidCatalogReferenceException("hardSkill", hs.getHardSkillId());
                }
            }
        }

        if (candidate.getSoftSkills() != null) {
            for (CandidateSoftSkill ss : candidate.getSoftSkills()) {
                if (ss.getSoftSkillId() != null && !catalogValidationPort.existsSoftSkill(ss.getSoftSkillId())) {
                    throw new InvalidCatalogReferenceException("softSkill", ss.getSoftSkillId());
                }
            }
        }

        if (candidate.getEducations() != null) {
            for (CandidateEducation edu : candidate.getEducations()) {
                if (edu.getEducationLevelId() != null && !catalogValidationPort.existsEducationLevel(edu.getEducationLevelId())) {
                    throw new InvalidCatalogReferenceException("educationLevel", edu.getEducationLevelId());
                }
            }
        }
    }

    private void saveProfessionalProfile(CandidateProfessionalProfile profile, Long candidateId) {
        if (profile == null) return;
        profile.setCandidateId(candidateId);
        profile.setCreatedAt(LocalDateTime.now());
        professionalProfileRepositoryPort.save(profile);
    }

    private void saveEducations(Set<CandidateEducation> educations, Long candidateId) {
        if (educations == null) return;
        for (CandidateEducation edu : educations) {
            edu.setCandidateId(candidateId);
            edu.setCreatedAt(LocalDateTime.now());
            educationRepositoryPort.save(edu);
        }
    }

    private void saveHardSkills(Set<CandidateHardSkill> hardSkills, Long candidateId) {
        if (hardSkills == null) return;
        for (CandidateHardSkill hs : hardSkills) {
            hs.setCandidateId(candidateId);
            hs.setSource("MANUAL");
            hs.setConfidence(BigDecimal.ONE);
            hs.setCreatedAt(LocalDateTime.now());
            hardSkillRepositoryPort.save(hs);
        }
    }

    private void saveSoftSkills(Set<CandidateSoftSkill> softSkills, Long candidateId) {
        if (softSkills == null) return;
        for (CandidateSoftSkill ss : softSkills) {
            ss.setCandidateId(candidateId);
            ss.setSource("MANUAL");
            ss.setConfidence(BigDecimal.ONE);
            ss.setCreatedAt(LocalDateTime.now());
            softSkillRepositoryPort.save(ss);
        }
    }

    private void saveInitialState(Long candidateId, Long recruiterId) {
        CandidateState state = new CandidateState();
        state.setCandidateId(candidateId);
        state.setState("NEW");
        state.setCreatedAt(LocalDateTime.now());
        state.setCreatedBy(recruiterId);
        stateRepositoryPort.save(state);
    }

    private Candidate loadFullCandidate(Long candidateId) {
        Candidate candidate = candidateRepositoryPort.findById(candidateId)
                .orElseThrow(() -> new RuntimeException("Candidate not found after creation"));

        candidate.setProfessionalProfile(
                professionalProfileRepositoryPort.findByCandidateId(candidateId).orElse(null)
        );

        candidate.setEducations(new LinkedHashSet<>(
                educationRepositoryPort.findByCandidateId(candidateId)
        ));

        candidate.setHardSkills(new LinkedHashSet<>(
                hardSkillRepositoryPort.findByCandidateId(candidateId)
        ));

        candidate.setSoftSkills(new LinkedHashSet<>(
                softSkillRepositoryPort.findByCandidateId(candidateId)
        ));

        candidate.setStates(new LinkedHashSet<>(
                stateRepositoryPort.findByCandidateIdOrderByCreatedAtDesc(candidateId)
        ));

        return candidate;
    }
}
