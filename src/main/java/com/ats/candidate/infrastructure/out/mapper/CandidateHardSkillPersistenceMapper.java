package com.ats.candidate.infrastructure.out.mapper;

import com.ats.candidate.domain.model.CandidateHardSkill;
import com.ats.candidate.infrastructure.out.entity.CandidateHardSkillEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CandidateHardSkillPersistenceMapper {

    @Mapping(target = "candidateId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    CandidateHardSkillEntity toEntity(CandidateHardSkill domain);

    CandidateHardSkill toDomain(CandidateHardSkillEntity entity);
}
