package com.ats.candidate.infrastructure.out.mapper;

import com.ats.candidate.domain.model.EducationLevel;
import com.ats.candidate.infrastructure.out.entity.EducationLevelEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EducationLevelPersistenceMapper {
    EducationLevel toDomain(EducationLevelEntity entity);
}
