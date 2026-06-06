package com.ats.candidate.infrastructure.in.web.mapper;

import com.ats.candidate.domain.model.CountryCode;
import com.ats.candidate.infrastructure.in.web.dto.CountryCodeResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CountryCodeWebMapper {
    CountryCodeResponse toResponse(CountryCode countryCode);
}
