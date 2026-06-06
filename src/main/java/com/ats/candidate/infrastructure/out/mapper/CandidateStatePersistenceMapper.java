package com.ats.candidate.infrastructure.out.mapper;

import com.ats.candidate.domain.model.CandidateState;
import com.ats.candidate.infrastructure.out.entity.CandidateStateEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CandidateStatePersistenceMapper {
    CandidateStateEntity toEntity(CandidateState candidateState);
    CandidateState toDomain(CandidateStateEntity entity);
}
