package com.ats.candidate.infrastructure.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Nivel de estudios.")
public record EducationLevelResponse(
        Long id,
        String name
) {
}
