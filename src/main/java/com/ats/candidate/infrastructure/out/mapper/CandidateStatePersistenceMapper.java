package com.ats.candidate.infrastructure.out.mapper;

import com.ats.candidate.domain.model.CandidateState;
import com.ats.candidate.infrastructure.out.entity.CandidateStateEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CandidateStatePersistenceMapper {

    @Mapping(target = "candidateId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    CandidateStateEntity toEntity(CandidateState domain);

    CandidateState toDomain(CandidateStateEntity entity);
}
