package com.ats.candidate.infrastructure.in.web.mapper;

import com.ats.candidate.domain.model.Candidate;
import com.ats.candidate.domain.model.CandidateEducation;
import com.ats.candidate.domain.model.CandidateHardSkill;
import com.ats.candidate.domain.model.CandidateProfessionalProfile;
import com.ats.candidate.domain.model.CandidateSoftSkill;
import com.ats.candidate.domain.model.CandidateState;
import com.ats.candidate.infrastructure.in.web.dto.CandidateEducationResponse;
import com.ats.candidate.infrastructure.in.web.dto.CandidateHardSkillResponse;
import com.ats.candidate.infrastructure.in.web.dto.CandidateProfessionalProfileResponse;
import com.ats.candidate.infrastructure.in.web.dto.CandidateResponse;
import com.ats.candidate.infrastructure.in.web.dto.CandidateSoftSkillResponse;
import com.ats.candidate.infrastructure.in.web.dto.CandidateStateResponse;
import com.ats.candidate.infrastructure.in.web.dto.CreateCandidateEducationRequest;
import com.ats.candidate.infrastructure.in.web.dto.CreateCandidateHardSkillRequest;
import com.ats.candidate.infrastructure.in.web.dto.CreateCandidateRequest;
import com.ats.candidate.infrastructure.in.web.dto.CreateCandidateSoftSkillRequest;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public abstract class CandidateWebMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "professionalProfile", ignore = true)
    @Mapping(target = "experiences", ignore = true)
    @Mapping(target = "educations", ignore = true)
    @Mapping(target = "certifications", ignore = true)
    @Mapping(target = "notes", ignore = true)
    @Mapping(target = "languages", ignore = true)
    @Mapping(target = "softSkills", ignore = true)
    @Mapping(target = "hardSkills", ignore = true)
    @Mapping(target = "attachments", ignore = true)
    @Mapping(target = "states", ignore = true)
    @Mapping(target = "availabilities", ignore = true)
    @Mapping(target = "parseResults", ignore = true)
    @Mapping(target = "auditEvents", ignore = true)
    public abstract Candidate toDomain(CreateCandidateRequest request);

    public CandidateResponse toResponse(Candidate candidate) {
        if (candidate == null) return null;
        return new CandidateResponse(
                candidate.getId(),
                candidate.getFirstName(),
                candidate.getLastName(),
                candidate.getEmail(),
                candidate.getPhone(),
                candidate.getIdentityDocument(),
                candidate.getCountryCode(),
                candidate.getCode(),
                candidate.getBirthDate(),
                candidate.getLocation(),
                candidate.getLinkedinUrl(),
                candidate.getGithubUrl(),
                candidate.getActive(),
                candidate.getCreatedBy(),
                candidate.getCreatedAt(),
                mapProfile(candidate.getProfessionalProfile()),
                mapEducations(candidate.getEducations()),
                mapHardSkills(candidate.getHardSkills()),
                mapSoftSkills(candidate.getSoftSkills()),
                mapCurrentState(candidate.getStates())
        );
    }

    @AfterMapping
    void mapProfessionalProfile(CreateCandidateRequest request, @MappingTarget Candidate candidate) {
        if (request.professionalProfile() != null) {
            var req = request.professionalProfile();
            var profile = new CandidateProfessionalProfile();
            profile.setHeadline(trim(req.headline()));
            profile.setSummary(trim(req.summary()));
            profile.setLatestPosition(trim(req.latestPosition()));
            profile.setYearsExperience(req.yearsExperience());
            candidate.setProfessionalProfile(profile);
        }
    }

    @AfterMapping
    void mapEducations(CreateCandidateRequest request, @MappingTarget Candidate candidate) {
        if (request.educations() != null) {
            Set<CandidateEducation> educations = new LinkedHashSet<>();
            for (CreateCandidateEducationRequest req : request.educations()) {
                var education = new CandidateEducation();
                education.setEducationLevelId(req.educationLevelId());
                education.setDegree(trim(req.degree()));
                education.setInstitution(trim(req.institution()));
                education.setStartDate(req.startDate());
                education.setEndDate(req.endDate());
                educations.add(education);
            }
            candidate.setEducations(educations);
        }
    }

    @AfterMapping
    void mapHardSkills(CreateCandidateRequest request, @MappingTarget Candidate candidate) {
        if (request.hardSkills() != null) {
            Set<CandidateHardSkill> skills = new LinkedHashSet<>();
            for (CreateCandidateHardSkillRequest req : request.hardSkills()) {
                var skill = new CandidateHardSkill();
                skill.setHardSkillId(req.hardSkillId());
                skill.setLevel(trim(req.level()));
                skill.setYearsExperience(req.yearsExperience());
                skills.add(skill);
            }
            candidate.setHardSkills(skills);
        }
    }

    @AfterMapping
    void mapSoftSkills(CreateCandidateRequest request, @MappingTarget Candidate candidate) {
        if (request.softSkills() != null) {
            Set<CandidateSoftSkill> skills = new LinkedHashSet<>();
            for (CreateCandidateSoftSkillRequest req : request.softSkills()) {
                var skill = new CandidateSoftSkill();
                skill.setSoftSkillId(req.softSkillId());
                skills.add(skill);
            }
            candidate.setSoftSkills(skills);
        }
    }

    private CandidateProfessionalProfileResponse mapProfile(CandidateProfessionalProfile profile) {
        if (profile == null) return null;
        return new CandidateProfessionalProfileResponse(
                profile.getId(),
                profile.getHeadline(),
                profile.getSummary(),
                profile.getLatestPosition(),
                profile.getYearsExperience(),
                null
        );
    }

    private List<CandidateEducationResponse> mapEducations(Set<CandidateEducation> educations) {
        if (educations == null) return Collections.emptyList();
        return educations.stream().map(e -> new CandidateEducationResponse(
                e.getId(),
                e.getEducationLevelId(),
                e.getDegree(),
                e.getInstitution(),
                e.getStartDate(),
                e.getEndDate()
        )).toList();
    }

    private List<CandidateHardSkillResponse> mapHardSkills(Set<CandidateHardSkill> hardSkills) {
        if (hardSkills == null) return Collections.emptyList();
        return hardSkills.stream().map(s -> new CandidateHardSkillResponse(
                s.getId(),
                s.getHardSkillId(),
                s.getLevel(),
                s.getYearsExperience(),
                s.getSource(),
                s.getConfidence()
        )).toList();
    }

    private List<CandidateSoftSkillResponse> mapSoftSkills(Set<CandidateSoftSkill> softSkills) {
        if (softSkills == null) return Collections.emptyList();
        return softSkills.stream().map(s -> new CandidateSoftSkillResponse(
                s.getId(),
                s.getSoftSkillId(),
                s.getSource(),
                s.getConfidence()
        )).toList();
    }

    private CandidateStateResponse mapCurrentState(Set<CandidateState> states) {
        if (states == null || states.isEmpty()) return null;
        CandidateState s = states.iterator().next();
        return new CandidateStateResponse(
                s.getId(),
                s.getState(),
                s.getCreatedAt(),
                s.getCreatedBy()
        );
    }

    private String trim(String value) {
        return value == null ? null : value.trim();
    }
}
