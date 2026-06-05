package com.ats.candidate.infrastructure.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Habilidad tecnica del postulante.")
public record CandidateHardSkillResponse(
        @Schema(description = "Identificador del registro.", example = "1")
        Long id,

        @Schema(description = "Identificador de la habilidad tecnica.", example = "1")
        Long hardSkillId,

        @Schema(description = "Nivel de dominio.", example = "Avanzado")
        String level,

        @Schema(description = "Anios de experiencia.", example = "5")
        Integer yearsExperience,

        @Schema(description = "Fuente de donde se obtuvo.", example = "MANUAL")
        String source,

        @Schema(description = "Nivel de confianza.", example = "1.0")
        BigDecimal confidence
) {
}
