package com.ats.candidate.infrastructure.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "Estudio del postulante.")
public record CandidateEducationResponse(
        Long id,
        Long educationLevelId,
        String degree,
        String institution,
        LocalDate startDate,
        LocalDate endDate
) {
}
