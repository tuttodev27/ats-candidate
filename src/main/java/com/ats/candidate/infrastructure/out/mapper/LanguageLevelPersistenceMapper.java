package com.ats.candidate.infrastructure.out.mapper;

import com.ats.candidate.domain.model.LanguageLevel;
import com.ats.candidate.infrastructure.out.entity.LanguageLevelEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LanguageLevelPersistenceMapper {
    LanguageLevel toDomain(LanguageLevelEntity entity);
}
