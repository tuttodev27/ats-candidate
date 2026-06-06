package com.ats.candidate.infrastructure.in.web.mapper;

import com.ats.candidate.domain.model.LanguageLevel;
import com.ats.candidate.infrastructure.in.web.dto.LanguageLevelResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LanguageLevelWebMapper {
    LanguageLevelResponse toResponse(LanguageLevel languageLevel);
}
