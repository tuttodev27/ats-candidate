package com.ats.candidate.infrastructure.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Habilidad blanda del postulante.")
public record CandidateSoftSkillResponse(
        Long id,
        Long softSkillId,
        String source,
        BigDecimal confidence
) {
}
