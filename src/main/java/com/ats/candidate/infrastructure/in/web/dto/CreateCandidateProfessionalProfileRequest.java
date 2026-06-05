package com.ats.candidate.infrastructure.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Perfil profesional del postulante.")
public record CreateCandidateProfessionalProfileRequest(
        @Schema(description = "Titulo o headline profesional.", example = "Ingeniero de Software Senior")
        String headline,

        @Schema(description = "Resumen profesional.", example = "Ingeniero con 8+ anos de experiencia...")
        String summary,

        @Schema(description = "Ultimo cargo ocupado.", example = "Tech Lead")
        String latestPosition,

        @Schema(description = "Anios de experiencia.", example = "8")
        Integer yearsExperience
) {
}
