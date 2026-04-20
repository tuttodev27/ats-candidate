package com.ats.candidate.infrastructure.in.web.mapper;

import com.ats.candidate.domain.model.Candidate;
import com.ats.candidate.infrastructure.in.web.dto.CandidateResponse;
import com.ats.candidate.infrastructure.in.web.dto.CreateCandidateRequest;
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
    Candidate toDomain(CreateCandidateRequest request);

    CandidateResponse toResponse(Candidate candidate);

    default String map(String value) {
        return value == null ? null : value.trim();
    }
}
