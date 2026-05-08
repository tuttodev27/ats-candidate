package com.ats.candidate.infrastructure.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Schema(description = "Habilidad blanda del postulante.")
public record CreateCandidateSoftSkillRequest(
        @NotNull
        Long softSkillId,

        String source,
        BigDecimal confidence
) {
}
