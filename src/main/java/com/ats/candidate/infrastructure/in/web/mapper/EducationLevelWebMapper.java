package com.ats.candidate.infrastructure.in.web.mapper;

import com.ats.candidate.domain.model.EducationLevel;
import com.ats.candidate.infrastructure.in.web.dto.EducationLevelResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EducationLevelWebMapper {
    EducationLevelResponse toResponse(EducationLevel educationLevel);
}
