package com.ats.candidate.infrastructure.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@Schema(description = "Habilidad tecnica del postulante.")
public record CreateCandidateHardSkillRequest(
        @NotNull
        Long hardSkillId,

        @Size(max = 80)
        String level,

        Integer yearsExperience,
        String source,
        BigDecimal confidence
) {
}
