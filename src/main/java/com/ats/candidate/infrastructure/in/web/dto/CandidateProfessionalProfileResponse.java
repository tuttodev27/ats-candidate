package com.ats.candidate.infrastructure.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Perfil profesional del postulante.")
public record CandidateProfessionalProfileResponse(
        Long id,
        String headline,
        String summary,
        String latestPosition,
        Long experienceRangeId,
        Integer yearsExperience
) {
}
