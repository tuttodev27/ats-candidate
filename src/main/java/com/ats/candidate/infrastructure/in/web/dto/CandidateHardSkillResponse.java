package com.ats.candidate.infrastructure.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Habilidad tecnica del postulante.")
public record CandidateHardSkillResponse(
        Long id,
        Long hardSkillId,
        String level,
        Integer yearsExperience,
        String source,
        BigDecimal confidence
) {
}
