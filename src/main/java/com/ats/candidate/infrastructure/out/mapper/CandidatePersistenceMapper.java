package com.ats.candidate.infrastructure.out.mapper;

import com.ats.candidate.domain.model.Candidate;
import com.ats.candidate.infrastructure.out.entity.CandidateEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CandidatePersistenceMapper {

    @Mapping(target = "email", expression = "java(normalizeEmail(candidate.getEmail()))")
    CandidateEntity toEntity(Candidate candidate);

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
    Candidate toDomain(CandidateEntity entity);

    default String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }
}
