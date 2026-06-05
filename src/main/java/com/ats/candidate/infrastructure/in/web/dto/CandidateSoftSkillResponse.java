package com.ats.candidate.infrastructure.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Habilidad blanda del postulante.")
public record CandidateSoftSkillResponse(
        @Schema(description = "Identificador del registro.", example = "1")
        Long id,

        @Schema(description = "Identificador de la habilidad blanda.", example = "1")
        Long softSkillId,

        @Schema(description = "Fuente de donde se obtuvo.", example = "MANUAL")
        String source,

        @Schema(description = "Nivel de confianza.", example = "1.0")
        BigDecimal confidence
) {
}
