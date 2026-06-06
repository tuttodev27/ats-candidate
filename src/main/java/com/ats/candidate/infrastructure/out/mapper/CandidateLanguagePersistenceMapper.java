package com.ats.candidate.infrastructure.out.mapper;

import com.ats.candidate.domain.model.CandidateLanguage;
import com.ats.candidate.infrastructure.out.entity.CandidateLanguageEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CandidateLanguagePersistenceMapper {
    CandidateLanguageEntity toEntity(CandidateLanguage language);
    CandidateLanguage toDomain(CandidateLanguageEntity entity);
}
