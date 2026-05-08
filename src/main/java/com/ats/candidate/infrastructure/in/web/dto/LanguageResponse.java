package com.ats.candidate.infrastructure.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Idioma.")
public record LanguageResponse(
        Long id,
        String name,
        String isoCode
) {
}
