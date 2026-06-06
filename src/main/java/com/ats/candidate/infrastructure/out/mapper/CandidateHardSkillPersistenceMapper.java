package com.ats.candidate.infrastructure.out.mapper;

import com.ats.candidate.domain.model.CandidateHardSkill;
import com.ats.candidate.infrastructure.out.entity.CandidateHardSkillEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CandidateHardSkillPersistenceMapper {
    CandidateHardSkillEntity toEntity(CandidateHardSkill hardSkill);
    CandidateHardSkill toDomain(CandidateHardSkillEntity entity);
}
