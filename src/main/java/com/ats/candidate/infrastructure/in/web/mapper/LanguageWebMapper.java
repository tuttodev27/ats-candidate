package com.ats.candidate.infrastructure.in.web.mapper;

import com.ats.candidate.domain.model.Language;
import com.ats.candidate.infrastructure.in.web.dto.LanguageResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LanguageWebMapper {
    LanguageResponse toResponse(Language language);
}
