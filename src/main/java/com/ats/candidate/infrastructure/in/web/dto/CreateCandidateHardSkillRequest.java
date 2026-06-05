package com.ats.candidate.infrastructure.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Habilidad tecnica del postulante.")
public record CreateCandidateHardSkillRequest(
        @NotNull
        @Schema(description = "Identificador de la habilidad tecnica.", example = "1")
        Long hardSkillId,

        @Schema(description = "Nivel de dominio.", example = "Avanzado")
        String level,

        @Schema(description = "Anios de experiencia.", example = "5")
        Integer yearsExperience
) {
}
