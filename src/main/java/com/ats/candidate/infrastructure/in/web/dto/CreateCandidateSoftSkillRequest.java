package com.ats.candidate.infrastructure.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Habilidad blanda del postulante.")
public record CreateCandidateSoftSkillRequest(
        @NotNull
        @Schema(description = "Identificador de la habilidad blanda.", example = "1")
        Long softSkillId
) {
}
