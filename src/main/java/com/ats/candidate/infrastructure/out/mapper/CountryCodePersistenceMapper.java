package com.ats.candidate.infrastructure.out.mapper;

import com.ats.candidate.domain.model.CountryCode;
import com.ats.candidate.infrastructure.out.entity.CountryCodeEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CountryCodePersistenceMapper {
    CountryCode toDomain(CountryCodeEntity entity);
}
