package com.ats.candidate.infrastructure.out.mapper;

import com.ats.candidate.domain.model.CandidateEducation;
import com.ats.candidate.infrastructure.out.entity.CandidateEducationEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CandidateEducationPersistenceMapper {
    CandidateEducationEntity toEntity(CandidateEducation education);
    CandidateEducation toDomain(CandidateEducationEntity entity);
}
