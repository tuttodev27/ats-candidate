package com.ats.candidate.infrastructure.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Codigo telefonico de pais.")
public record CountryCodeResponse(
        Long id,
        String countryName,
        String isoCode,
        String phoneCode
) {
}
