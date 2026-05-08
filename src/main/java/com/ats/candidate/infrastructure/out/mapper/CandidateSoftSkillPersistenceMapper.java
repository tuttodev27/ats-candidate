package com.ats.candidate.infrastructure.out.mapper;

import com.ats.candidate.domain.model.CandidateSoftSkill;
import com.ats.candidate.infrastructure.out.entity.CandidateSoftSkillEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CandidateSoftSkillPersistenceMapper {
    CandidateSoftSkillEntity toEntity(CandidateSoftSkill softSkill);
    CandidateSoftSkill toDomain(CandidateSoftSkillEntity entity);
}
