package com.ats.candidate.infrastructure.out.mapper;

import com.ats.candidate.domain.model.CandidateEducation;
import com.ats.candidate.infrastructure.out.entity.CandidateEducationEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CandidateEducationPersistenceMapper {

    @Mapping(target = "candidateId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    CandidateEducationEntity toEntity(CandidateEducation domain);

    CandidateEducation toDomain(CandidateEducationEntity entity);
}
