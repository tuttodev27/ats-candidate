package com.ats.candidate.infrastructure.out.mapper;

import com.ats.candidate.domain.model.CandidateProfessionalProfile;
import com.ats.candidate.infrastructure.out.entity.CandidateProfessionalProfileEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CandidateProfessionalProfilePersistenceMapper {
    CandidateProfessionalProfileEntity toEntity(CandidateProfessionalProfile professionalProfile);
    CandidateProfessionalProfile toDomain(CandidateProfessionalProfileEntity entity);
}
