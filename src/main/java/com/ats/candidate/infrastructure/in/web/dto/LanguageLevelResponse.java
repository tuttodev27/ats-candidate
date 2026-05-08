package com.ats.candidate.infrastructure.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Nivel de idioma.")
public record LanguageLevelResponse(
        Long id,
        String code,
        String name
) {
}
