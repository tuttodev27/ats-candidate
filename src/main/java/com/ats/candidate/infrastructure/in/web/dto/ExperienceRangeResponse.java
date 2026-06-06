package com.ats.candidate.infrastructure.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Rango de anos de experiencia.")
public record ExperienceRangeResponse(
        Long id,
        String label,
        Integer minYears,
        Integer maxYears
) {
}
