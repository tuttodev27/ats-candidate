package com.ats.candidate.infrastructure.out.mapper;

import com.ats.candidate.domain.model.CandidateExperience;
import com.ats.candidate.infrastructure.out.entity.CandidateExperienceEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CandidateExperiencePersistenceMapper {
    CandidateExperienceEntity toEntity(CandidateExperience experience);
    @org.mapstruct.Mapping(target = "descriptions", ignore = true)
    @org.mapstruct.Mapping(target = "technologies", ignore = true)
    CandidateExperience toDomain(CandidateExperienceEntity entity);
}

