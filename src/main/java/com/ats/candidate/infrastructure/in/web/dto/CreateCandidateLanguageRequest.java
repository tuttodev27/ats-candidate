package com.ats.candidate.infrastructure.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Schema(description = "Idioma del postulante.")
public record CreateCandidateLanguageRequest(
        @NotNull
        Long languageId,

        Long languageLevelId,
        String source,
        BigDecimal confidence
) {
}
