package com.ats.candidate.infrastructure.out.mapper;

import com.ats.candidate.domain.model.ExperienceRange;
import com.ats.candidate.infrastructure.out.entity.ExperienceRangeEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ExperienceRangePersistenceMapper {
    ExperienceRange toDomain(ExperienceRangeEntity entity);
}
