package com.ats.candidate.infrastructure.in.web.mapper;

import com.ats.candidate.domain.model.Candidate;
import com.ats.candidate.domain.model.Attachment;
import com.ats.candidate.domain.model.CandidateEducation;
import com.ats.candidate.domain.model.CandidateHardSkill;
import com.ats.candidate.domain.model.CandidateLanguage;
import com.ats.candidate.domain.model.CandidateProfessionalProfile;
import com.ats.candidate.domain.model.CandidateSoftSkill;
import com.ats.candidate.domain.model.CandidateExperience;
import com.ats.candidate.domain.model.CandidateNote;
import com.ats.candidate.infrastructure.in.web.dto.CandidateEducationResponse;
import com.ats.candidate.infrastructure.in.web.dto.CandidateHardSkillResponse;
import com.ats.candidate.infrastructure.in.web.dto.CandidateLanguageResponse;
import com.ats.candidate.infrastructure.in.web.dto.CandidateProfessionalProfileResponse;
import com.ats.candidate.infrastructure.in.web.dto.CandidateResponse;
import com.ats.candidate.infrastructure.in.web.dto.CandidateSoftSkillResponse;
import com.ats.candidate.infrastructure.in.web.dto.AttachmentResponse;
import com.ats.candidate.infrastructure.in.web.dto.CreateCandidateEducationRequest;
import com.ats.candidate.infrastructure.in.web.dto.CreateCandidateHardSkillRequest;
import com.ats.candidate.infrastructure.in.web.dto.CreateCandidateLanguageRequest;
import com.ats.candidate.infrastructure.in.web.dto.CreateCandidateProfessionalProfileRequest;
import com.ats.candidate.infrastructure.in.web.dto.CreateCandidateRequest;
import com.ats.candidate.infrastructure.in.web.dto.CreateCandidateSoftSkillRequest;
import com.ats.candidate.infrastructure.in.web.dto.UpdateCandidateRequest;
import com.ats.candidate.infrastructure.in.web.dto.CandidateExperienceRequest;
import com.ats.candidate.infrastructure.in.web.dto.CandidateNoteRequest;
import com.ats.candidate.infrastructure.in.web.dto.CandidateExperienceResponse;
import com.ats.candidate.infrastructure.in.web.dto.CandidateNoteResponse;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CandidateWebMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "experiences", ignore = true)
    @Mapping(target = "certifications", ignore = true)
    @Mapping(target = "notes", ignore = true)
    @Mapping(target = "attachments", ignore = true)
    @Mapping(target = "states", ignore = true)
    @Mapping(target = "availabilities", ignore = true)
    @Mapping(target = "parseResults", ignore = true)
    @Mapping(target = "auditEvents", ignore = true)
    Candidate toDomain(CreateCandidateRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "firstName", ignore = true)
    @Mapping(target = "lastName", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "code", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "certifications", ignore = true)
    @Mapping(target = "attachments", ignore = true)
    @Mapping(target = "states", ignore = true)
    @Mapping(target = "availabilities", ignore = true)
    @Mapping(target = "parseResults", ignore = true)
    @Mapping(target = "auditEvents", ignore = true)
    Candidate toDomain(UpdateCandidateRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "candidateId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "descriptions", ignore = true)
    @Mapping(target = "technologies", ignore = true)
    CandidateExperience toDomain(CandidateExperienceRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "candidateId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    CandidateNote toDomain(CandidateNoteRequest request);


    @Mapping(target = "currentState",
             expression = "java(candidate.getStates() != null && !candidate.getStates().isEmpty() ? candidate.getStates().iterator().next().getState() : null)")
    CandidateResponse toResponse(Candidate candidate);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "candidateId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    CandidateProfessionalProfile toDomain(CreateCandidateProfessionalProfileRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "candidateId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    CandidateEducation toDomain(CreateCandidateEducationRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "candidateId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    CandidateLanguage toDomain(CreateCandidateLanguageRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "candidateId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    CandidateHardSkill toDomain(CreateCandidateHardSkillRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "candidateId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    CandidateSoftSkill toDomain(CreateCandidateSoftSkillRequest request);

    CandidateProfessionalProfileResponse toResponse(CandidateProfessionalProfile professionalProfile);
    CandidateEducationResponse toResponse(CandidateEducation education);
    CandidateLanguageResponse toResponse(CandidateLanguage language);
    CandidateHardSkillResponse toResponse(CandidateHardSkill hardSkill);
    CandidateSoftSkillResponse toResponse(CandidateSoftSkill softSkill);
    AttachmentResponse toResponse(Attachment attachment);
    CandidateExperienceResponse toResponse(CandidateExperience experience);
    CandidateNoteResponse toResponse(CandidateNote note);


    default String map(String value) {
        return value == null ? null : value.trim();
    }
}
