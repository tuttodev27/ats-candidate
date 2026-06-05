package com.ats.candidate.infrastructure.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Schema(description = "Estudio o formacion academica del postulante.")
public record CreateCandidateEducationRequest(
        @NotNull
        @Schema(description = "Identificador del nivel educativo.", example = "4")
        Long educationLevelId,

        @Schema(description = "Titulo obtenido.", example = "Ingenieria Civil en Computacion")
        String degree,

        @Schema(description = "Institucion educativa.", example = "Universidad de Chile")
        String institution,

        @Schema(description = "Fecha de inicio.", example = "2010-03-01")
        LocalDate startDate,

        @Schema(description = "Fecha de termino.", example = "2015-12-31")
        LocalDate endDate
) {
}
