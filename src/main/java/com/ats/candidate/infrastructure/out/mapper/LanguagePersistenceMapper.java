package com.ats.candidate.infrastructure.out.mapper;

import com.ats.candidate.domain.model.Language;
import com.ats.candidate.infrastructure.out.entity.LanguageEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LanguagePersistenceMapper {
    Language toDomain(LanguageEntity entity);
}
