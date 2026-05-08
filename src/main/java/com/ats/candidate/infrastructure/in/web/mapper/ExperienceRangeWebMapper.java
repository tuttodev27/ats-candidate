package com.ats.candidate.infrastructure.in.web.mapper;

import com.ats.candidate.domain.model.ExperienceRange;
import com.ats.candidate.infrastructure.in.web.dto.ExperienceRangeResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ExperienceRangeWebMapper {
    ExperienceRangeResponse toResponse(ExperienceRange experienceRange);
}
