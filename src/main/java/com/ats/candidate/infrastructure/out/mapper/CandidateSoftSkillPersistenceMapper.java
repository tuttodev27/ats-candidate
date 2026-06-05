package com.ats.candidate.infrastructure.out.mapper;

import com.ats.candidate.domain.model.CandidateSoftSkill;
import com.ats.candidate.infrastructure.out.entity.CandidateSoftSkillEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CandidateSoftSkillPersistenceMapper {

    @Mapping(target = "candidateId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    CandidateSoftSkillEntity toEntity(CandidateSoftSkill domain);

    CandidateSoftSkill toDomain(CandidateSoftSkillEntity entity);
}
