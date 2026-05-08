package com.ats.candidate.infrastructure.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

@Schema(description = "Perfil profesional del postulante.")
public record CreateCandidateProfessionalProfileRequest(
        @Size(max = 180)
        String headline,

        @Size(max = 4000)
        String summary,

        @Size(max = 180)
        String latestPosition,

        Long experienceRangeId,

        Integer yearsExperience
) {
}
