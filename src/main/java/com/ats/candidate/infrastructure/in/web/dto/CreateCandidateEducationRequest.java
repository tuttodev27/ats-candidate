package com.ats.candidate.infrastructure.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Schema(description = "Estudio del postulante.")
public record CreateCandidateEducationRequest(
        @NotNull
        Long educationLevelId,

        @Size(max = 180)
        String degree,

        @Size(max = 180)
        String institution,

        LocalDate startDate,
        LocalDate endDate
) {
}
