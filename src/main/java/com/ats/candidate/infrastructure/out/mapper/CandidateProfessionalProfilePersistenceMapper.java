package com.ats.candidate.infrastructure.out.mapper;

import com.ats.candidate.domain.model.CandidateProfessionalProfile;
import com.ats.candidate.infrastructure.out.entity.CandidateProfessionalProfileEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CandidateProfessionalProfilePersistenceMapper {

    @Mapping(target = "candidateId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    CandidateProfessionalProfileEntity toEntity(CandidateProfessionalProfile domain);

    CandidateProfessionalProfile toDomain(CandidateProfessionalProfileEntity entity);
}
