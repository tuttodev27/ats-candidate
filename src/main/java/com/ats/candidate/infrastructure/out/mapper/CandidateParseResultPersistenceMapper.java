package com.ats.candidate.infrastructure.out.mapper;

import com.ats.candidate.domain.model.CandidateParseResult;
import com.ats.candidate.infrastructure.out.entity.CandidateParseResultEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CandidateParseResultPersistenceMapper {
    CandidateParseResultEntity toEntity(CandidateParseResult domain);
    CandidateParseResult toDomain(CandidateParseResultEntity entity);
}
